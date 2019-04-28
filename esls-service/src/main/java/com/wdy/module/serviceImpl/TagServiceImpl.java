package com.wdy.module.serviceImpl;

import com.wdy.module.common.constant.*;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.common.response.*;
import com.wdy.module.cycleJob.DynamicTask;
import com.wdy.module.dao.*;
import com.wdy.module.entity.*;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("TagService")
@Slf4j
public class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService<Tag> {
    // 更新标签样式
    @Override
    public ResponseBean updateTagStyle(Tag tag, boolean isWaitingLong) {
        int waitingTime = 300;
        if (isWaitingLong == true)
            waitingTime = Integer.valueOf(SystemVersionArgs.commandWaitingTime);
        try {
            if (tag == null)
                throw new ServiceException(ResultEnum.TAG_NOT_EXIST);
            Channel channel = SocketChannelHelper.getChannelByRouter(tag.getRouter().getId());
            if (channel == null)
                throw new ServiceException(ResultEnum.COMMUNITICATION_ERROR);
            Good good = tag.getGood();
            if (good == null)
                throw new ServiceException(ResultEnum.TAG_BINDED_GOOD_EMPTY);
            String regionNames = good.getRegionNames();
            boolean isRegion = !StringUtil.isEmpty(regionNames) ? true : false;
            ByteResponse byteResponse = ImageHelper.getByteResponse(tag);
            if (byteResponse == null)
                return new ResponseBean(1, 0);
            String resultString;
            // 样式具体内容分包
            List<byte[]> byteList = byteResponse.getByteList();
            if (!isRegion) {
                // 更改样式全局信息包
                resultString = nettyUtil.sendMessageWithRepeat(channel, CommandConstant.getBytesByType(SpringContextUtil.getAddressByBarCode(tag.getBarCode()), byteResponse.getFirstByte(), CommandConstant.COMMANDTYPE_TAG), Integer.valueOf(SystemVersionArgs.commandRepeatTime), waitingTime);
                System.out.println("更改样式全局信息包命令响应结果：" + resultString);
                if (ErrorUtil.isErrorCommunication(resultString)) {
                    return new ResponseBean(1, 0);
                }
            }
            for (int i = 0; i < byteList.size(); i++) {
                if (i == 0)
                    resultString = nettyUtil.sendMessageWithRepeat(channel, CommandConstant.getBytesByType(SpringContextUtil.getAddressByBarCode(tag.getBarCode()), byteList.get(i), CommandConstant.COMMANDTYPE_TAG), Integer.valueOf(SystemVersionArgs.commandRepeatTime), waitingTime);
                else
                    resultString = nettyUtil.sendMessageWithRepeat(channel, CommandConstant.getBytesByType(SpringContextUtil.getAddressByBarCode(tag.getBarCode()), byteList.get(i), CommandConstant.COMMANDTYPE_TAG), Integer.valueOf(SystemVersionArgs.commandRepeatTime), 300);
                System.out.println("样式具体内容分包命令" + i + "响应结果：" + resultString);
                if (ErrorUtil.isErrorCommunication(resultString)) {
                    return new ResponseBean(1, 0);
                }
            }
        } catch (Exception e) {
            throw new ServiceException(-1, "TagServiceImpl-updateTagStyle-" + e);
        }
        return new ResponseBean(1, 1);
    }

    // 刷新指定标签
    @Override
    public ResponseBean flushTags(RequestBean requestBean) throws ServiceException {
        String contentType = CommandConstant.FLUSH;
        List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
        ResponseBean responseBean;
        responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
        return responseBean;
    }

    // 刷新指定路由器下的所有标签
    @Override
    public ResponseBean flushTagsByRouter(RequestBean requestBean) {
        String contentType = CommandConstant.FLUSH;
        List<Router> routers = RequestBeanUtil.getRoutersByRequestBean(requestBean);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithRouters(routers, contentType, CommandConstant.COMMANDTYPE_TAG_BROADCAST);
        return responseBean;
    }

    // 定期刷新
    @Override
    public ResponseBean flushTagsByCycle(RequestBean requestBean, Integer mode) {
        // 设置定期刷新
        CycleJob cyclejob = new CycleJob();
        cyclejob.setCron("无效字段");
        if (mode == 2)
            cyclejob.setDescription("对指定标签定期刷新");
        else
            cyclejob.setDescription("对路由器下所有标签定期刷新");
        cyclejob.setArgs(RequestBeanUtil.getRequestBeanAsString(requestBean));
        cyclejob.setMode(mode);
        cyclejob.setType(ModeConstant.DO_BY_TAG_FLUSH);
        cycleJobDao.save(cyclejob);
        dynamicTask.addTask(requestBean, ModeConstant.DO_BY_TAG_FLUSH, mode);
        return new ResponseBean(requestBean.getItems().size(), requestBean.getItems().size());
    }

    // 巡检指定地址的标签
    @Override
    public ResponseBean scanTags(RequestBean requestBean) {
        String contentType = CommandConstant.QUERYTAG;
        List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
        TagUtil.setTagIsNotWorking(tags);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
        return responseBean;
    }

    // 巡检指定路由器下的所有标签(广播命令只发一次)
    @Override
    public ResponseBean scanTagsByRouter(RequestBean requestBean) {
        String contentType = CommandConstant.QUERYTAG;
        List<Router> routers = RequestBeanUtil.getRoutersByRequestBean(requestBean);
        List<Tag> tags = TagUtil.getTagsByRouters(routers);
        TagUtil.setTagIsNotWorking(tags);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithRouters(routers, contentType, CommandConstant.COMMANDTYPE_TAG_BROADCAST);
        return responseBean;
    }

    // 定期巡检
    @Override
    public ResponseBean scanTagsByCycle(RequestBean requestBean, Integer mode) {
        // 设置定期巡检
        CycleJob cyclejob = new CycleJob();
        // cron表达式
        cyclejob.setCron("无效字段");
        if (mode == 2)
            cyclejob.setDescription("对指定标签定期巡检");
        else
            cyclejob.setDescription("对路由器下所有标签定期巡检");
        cyclejob.setArgs(RequestBeanUtil.getRequestBeanAsString(requestBean));
        cyclejob.setMode(mode);
        cyclejob.setType(ModeConstant.DO_BY_TAG_SCAN);
        cycleJobDao.save(cyclejob);
        dynamicTask.addTask(requestBean, ModeConstant.DO_BY_TAG_SCAN, mode);
        return new ResponseBean(requestBean.getItems().size(), requestBean.getItems().size());
    }

    // 闪灯或者结束闪灯
    @Override
    public ResponseBean changeLightStatus(RequestBean requestBean, Integer mode) {
        String contentType = null;
        if (mode == 0) {
            log.info("向指定的信息集合发送结束闪灯命令");
            contentType = CommandConstant.TAGBLINGOVER;
        } else if (mode == 1) {
            log.info("向指定的信息集合发送闪灯命令");
            contentType = CommandConstant.TAGBLING;
        }
        List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
        return responseBean;
    }

    // 对路由器下的所有标签闪灯或者结束闪灯
    @Override
    public ResponseBean changeLightStatusByRouter(RequestBean requestBean, Integer mode) {
        ResponseBean responseBean = null;
        try {
            String contentType = null;
            if (mode == 0) {
                log.info("向指定的信息集合发送结束闪灯命令");
                contentType = CommandConstant.TAGBLINGOVER;
            } else if (mode == 1) {
                log.info("向指定的信息集合发送闪灯命令");
                contentType = CommandConstant.TAGBLING;
            }
            List<Router> routers = RequestBeanUtil.getRoutersByRequestBean(requestBean);
            responseBean = SendCommandUtil.sendCommandWithRouters(routers, contentType, CommandConstant.COMMANDTYPE_TAG_BROADCAST);
        } catch (Exception e) {
            System.out.println(e);
        }
        return responseBean;
    }

    //绑定商品和标签
    @Override
    public ResponseEntity<ResultBean> bindGoodAndTag(String sourceArgs1, String ArgsString1, String sourceArgs2, String ArgsString2, String mode) {
        // 获取商品实体
        List<Good> goods = findByArrtribute(TableConstant.TABLE_GOODS, sourceArgs1, ArgsString1, Good.class);
        // 获取标签实体
        List<Tag> tagList = findByArrtribute(TableConstant.TABLE_TAGS, sourceArgs2, ArgsString2, Tag.class);
        ResponseEntity<ResultBean> result;
        if ((result = ResponseUtil.testListSize("没有相应的标签或商品 请重新选择", goods, tagList)) != null) return result;
        // 修改标签实体的goodid state
        if (goods.size() > 1 || tagList.size() > 1)
            return new ResponseEntity<>(ResultBean.error(" 根据字段获取的数据不唯一 请选择唯一字段 "), HttpStatus.BAD_REQUEST);
        Good good = goods.get(0);
        // regionNames置空
        good.setRegionNames(null);
        good.setWaitUpdate(1);
        goodDao.save(good);
        Tag tag = tagList.get(0);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
        // 换绑
        if ("2".equals(mode)) {
            tag.setState((byte) 1);
            tag.setGood(good);
            saveOne(tag);
            SendCommandUtil.updateTagStyle(tags, false, false);
        }
        if (tag.getGood() == null) {
            if (mode.equals("1"))
                tag.setGood(good);
            else
                return new ResponseEntity<>(ResultBean.error(" 商品和标签暂未绑定 请改变mode重新发送请求 "), HttpStatus.BAD_REQUEST);
        } else if (tag.getGood() != null && mode.equals("1"))
            return new ResponseEntity<>(ResultBean.error(" 此标签已经绑定商品 请重新选择标签 "), HttpStatus.BAD_REQUEST);
        try {
            // 绑定
            if (mode.equals("1")) {
                tag.setState((byte) 1);
                saveOne(tag);
                String contentType = CommandConstant.TAGBIND;
                SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
                SendCommandUtil.updateTagStyle(tags, false, false);
            }
            // 解绑
            else if (mode.equals("0")) {
                tag.setState((byte) 0);
                tag.setWaitUpdate(1);
                tag.setGood(null);
                saveOne(tag);
                String contentType = CommandConstant.TAGBINDOVER;
                SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
            }
        } catch (Exception e) {
            System.out.println("发送样式修改包失败");
        }
        // 返回前端提示信息
        return new ResponseEntity<>(ResultBean.success(mode.equals("0") ? "取消绑定成功" : "绑定成功"), HttpStatus.OK);
    }

    // 标签移除 进入休眠状态
    @Override
    public ResponseBean removeTagCommand(RequestBean requestBean, Integer mode) {
        String contentType = CommandConstant.TAGREMOVE;
        ResponseBean responseBean = null;
        if (mode == 0) {
            List<Tag> tags = RequestBeanUtil.getTagsByRequestBean(requestBean);
            for (Tag tag : tags) {
                tag.setIsWorking((byte) 0);
                tag.setGood(null);
                saveOne(tag);

            }
            responseBean = SendCommandUtil.sendCommandWithTags(tags, contentType, CommandConstant.COMMANDTYPE_TAG);
        } else if (mode == 1) {
            List<Tag> tags = new ArrayList<>();
            List<Router> routers = RequestBeanUtil.getRoutersByRequestBean(requestBean);
            for (Router r : routers) {
                List<Tag> byRouterId = findByRouterId(r.getId());
                tags.addAll(byRouterId);
            }
            for (Tag tag : tags) {
                tag.setIsWorking((byte) 0);
                tag.setGood(null);
                tag.setForbidState(1);
                saveOne(tag);
            }
            responseBean = SendCommandUtil.sendCommandWithRouters(routers, contentType, CommandConstant.COMMANDTYPE_TAG_BROADCAST);
        }
        return responseBean;
    }

    // 标签更改样式
    @Override
    public ResponseEntity<ResultBean> updateTagStyleById(long tagId, long styleId, Integer mode) {
        // 修改标签实体对应的styleId
        List<Tag> tagList = findByArrtribute(TableConstant.TABLE_TAGS, ArrtributeConstant.TABLE_ID, String.valueOf(tagId), Tag.class);
        if (tagList.size() <= 0)
            return new ResponseEntity<>(ResultBean.error("没有相应ID的标签 请重新选择"), HttpStatus.BAD_REQUEST);
        Tag tag = tagList.get(0);
        Good good = tag.getGood();
        if (good == null)
            return new ResponseEntity<>(ResultBean.error("标签绑定商品为空"), HttpStatus.BAD_REQUEST);
        else {
            good.setWaitUpdate(1);
            good.setRegionNames(null);
            goodDao.save(good);
        }
        if (tag.getStyle() != null && tag.getStyle().getId() == styleId)
            return new ResponseEntity<>(ResultBean.error("标签对应的样式一致,无需更改"), HttpStatus.BAD_REQUEST);
        else {
            Style one = styleDao.getOne(styleId);
            tag.setStyle(one);
            saveOne(tag);
            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            try {
                ResponseBean responseBean = new ResponseBean(1, 1);
                if (mode == 1)
                    responseBean = SendCommandUtil.updateTagStyle(tags, false, false);
                return new ResponseEntity<>(ResultBean.success("样式更换成功(通信成功数：" + responseBean.getSuccessNumber() + ")"), HttpStatus.OK);
            } catch (Exception e) {
                System.out.println("发送样式修改包失败" + e);
            }
            return new ResponseEntity<>(ResultBean.error("发送样式修改包失败"), HttpStatus.BAD_REQUEST);
        }
    }

    // 改变标签状态
    @Override
    public ResponseBean changeStatus(RequestBean requestBean, Integer mode) {
        int sum = 0, successNumber = 0;
        for (RequestItem items : requestBean.getItems()) {
            List<Tag> tagItem = findByArrtribute(TableConstant.TABLE_TAGS, items.getQuery(), items.getQueryString(), Tag.class);
            for (Tag tag : tagItem) {
                sum++;
                if (!tag.getForbidState().equals(mode)) {
                    tag.setForbidState(mode);
                    tag.setIsWorking((byte) mode.intValue());
                    Tag resultTag = saveOne(tag);
                    if (resultTag != null)
                        successNumber++;
                }
            }
        }
        return new ResponseBean(sum, successNumber);
    }

    @Override
    public List<Tag> findAll() {
        return tagDao.findAll();
    }

    @Override
    public List<Tag> findAll(Integer page, Integer count) {
        List<Tag> content = tagDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }

    @Override
    public List<Tag> findByRouterId(Long routerId) {
        return tagDao.findByRouterId(routerId);
    }

    @Override
    public Tag findByTagAddress(String tagAddress) {
        return tagDao.findByTagAddress(tagAddress);
    }

    @Override
    public Tag findByBarCode(String barCode) {
        return tagDao.findByBarCode(barCode);
    }

    @Override
    public Tag saveOne(Tag tag) {
        tag.setTagAddress(ByteUtil.getMergeMessage(SpringContextUtil.getAddressByBarCode(tag.getBarCode())));
        return tagDao.save(tag);
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return tagDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            tagDao.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired
    private TagDao tagDao;
    @Autowired
    private GoodDao goodDao;
    @Autowired
    private StyleDao styleDao;
    @Autowired
    private NettyUtil nettyUtil;
    @Autowired
    private CycleJobDao cycleJobDao;
    @Autowired
    private DynamicTask dynamicTask;
}

package com.wdy.module.serviceImpl;

import com.wdy.module.common.constant.ModeConstant;
import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.cycleJob.DynamicTask;
import com.wdy.module.dao.*;
import com.wdy.module.entity.*;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.RouterService;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.system.SystemVersionArgs;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service("RouterService")
public class RouterServiceImpl extends BaseServiceImpl implements RouterService {
    @Autowired
    private RouterDao routerDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private NettyUtil nettyUtil;
    @Autowired
    private CycleJobDao cycleJobDao;
    @Autowired
    private DynamicTask dynamicTask;

    @Override
    public List<Router> findAll() {
        return routerDao.findAll();
    }
    @Override
    public List<Router> findAll(Integer page, Integer count) {
        List<Router> content = routerDao.findAll(PageRequest.of(page, count, Sort.Direction.DESC, "id")).getContent();
        return content;
    }
    @Override
    @Transactional
    public Router saveOne(Router router) {
        return routerDao.save(router);
    }

    @Override
    public Optional<Router> findById(Long id) {
        return routerDao.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try{
            List<Tag> tagList = tagDao.findByRouterId(id);
            if(tagList!=null && tagList.size()>0)
                return false;
            routerDao.deleteById(id);
            return true;
        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    @Override
    public Router findByIp(String ip) {
        return routerDao.findByIp(ip);
    }

    @Override
    public Router findByOutNetIpAndPort(String ip,Integer port) {
        return routerDao.findByOutNetIpAndPort(ip,port);
    }

    @Override
    public Router findByBarCode(String barCode) {
        return routerDao.findByBarCode(barCode);
    }

    @Override
    public ResponseBean changeRouter(String sourceQuery, String sourceQueryString, String targetQuery, String targetQueryString) {
        Router source = (Router)findByArrtribute(TableConstant.TABLE_ROUTERS, sourceQuery, sourceQueryString, Router.class).get(0);
        Router target = (Router)findByArrtribute(TableConstant.TABLE_ROUTERS, targetQuery, targetQueryString, Router.class).get(0);
        target.setChannelId(source.getChannelId());
        if(source==null  || target == null)
            return new ResponseBean(0, 0);
        // 选用该样式的所有标签
        List<Tag> tags = tagDao.findByRouterId(source.getId());
        Long targetId = target.getId();
        int sum = tags.size();
        int successNumber = 0;
        // 更换标签路由器ID
        for(Tag tag : tags){
            Router router = new Router();
            router.setId(targetId);
            tag.setRouter(router);
            Tag save = tagDao.save(tag);
            if(save!=null)
                successNumber++;
        }
        return new ResponseBean(sum, successNumber);
    }

    // 路由器巡检
    @Override
    public ResponseBean routerScan(RequestBean requestBean) {
        String contentType = CommandConstant.QUERYROUTER;
        List<Router> routerList = RequestBeanUtil.getRoutersByRequestBean(requestBean);
        TagUtil.setRouterIsNotWorking(routerList);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithRouters(routerList, contentType,CommandConstant.COMMANDTYPE_ROUTER);
        return responseBean;
    }

    @Override
    public ResponseBean routersScan() {
        String contentType = CommandConstant.QUERYROUTER;
        List<Router> routers = findAll();
        List<Router> workingRouter = new ArrayList<>();
        for(Router r:routers)
            if(r.getState()==1)
                workingRouter.add(r);
        TagUtil.setRouterIsNotWorking(workingRouter);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithRouters(workingRouter, contentType,CommandConstant.COMMANDTYPE_ROUTER);
        return responseBean;
    }

    @Override
    public ResponseBean routerScanByCycle(RequestBean requestBean) {
        // 设置定期巡检
        CycleJob cyclejob = new CycleJob();
        // cron表达式
        cyclejob.setCron("无效字段");
        cyclejob.setDescription("对路由器定期巡检");
        cyclejob.setArgs(RequestBeanUtil.getRequestBeanAsString(requestBean));
        cyclejob.setMode(ModeConstant.DO_BY_ROUTER);
        cyclejob.setType(ModeConstant.DO_BY_ROUTER_SCAN);
        cycleJobDao.save(cyclejob);
        dynamicTask.addTask(requestBean,ModeConstant.DO_BY_ROUTER_SCAN,ModeConstant.DO_BY_ROUTER);
        return new ResponseBean(requestBean.getItems().size(), requestBean.getItems().size());
    }

    @Override
    public ResponseBean settingRouter(RequestBean requestBean) {
        List<Router> routerList = new ArrayList<>();
        for (RequestItem items : requestBean.getItems()) {
            routerList.addAll(findByArrtribute(TableConstant.TABLE_ROUTERS, items.getQuery(), items.getQueryString(), Router.class));
        }
        ResponseBean responseBean = SendCommandUtil.sendCommandWithSettingRouters(routerList);
        return responseBean;
    }

    @Override
    public ResponseBean routerRemove(RequestBean requestBean) {
        String contentType = CommandConstant.ROUTERREMOVE;
        List<Router> routerList = RequestBeanUtil.getRoutersByRequestBean(requestBean);
        ResponseBean responseBean = SendCommandUtil.sendCommandWithRouters(routerList, contentType,CommandConstant.COMMANDTYPE_ROUTER);
        return responseBean;
    }

    public Router updateRouter(Router router) {
        Router r = findById(router.getId()).get();
        router.setCompleteTime(new Timestamp(System.currentTimeMillis()));
        // 更新路由器 发送设置命令
        if(router.getId()!=0 ){
            //getBytesByType
            byte[] message = new byte[16];
            message[0]=0x02;
            message[1]=0x05;
            message[2]=0x0D;
            // mac地址
            byte[] mac = setAttribute("mac", r, router, 6);
            for(int i = 0 ;i<mac.length;i++)
                message[3+i] = mac[i];
            // IP地址
            if(!router.getIp().equals(r.getIp())){
                String ip = router.getIp();
                String[] ips = ip.split("\\.");
                for(int i=0;i<4;i++)
                    message[9+i] = (byte) Integer.parseInt(ips[i]);
            }
            // 信道
            if(!router.getChannelId().equals(r.getChannelId())){
                message[13] = Byte.parseByte(router.getChannelId());
            }
            // 频率
            byte[] frequency = setAttribute("frequency", r, router, 2);
            for(int i = 0 ;i<frequency.length;i++)
                message[14+i] = frequency[i];
            byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
            Channel channel = SocketChannelHelper.getChannelByRouter(r);
            String result = nettyUtil.sendMessageWithRepeat(channel, realMessage,Integer.valueOf(SystemVersionArgs.commandRepeatTime),Integer.valueOf(SystemVersionArgs.commandWaitingTime));
            if(result!=null && result.equals("成功")){
                System.out.println("路由器设置成功");
            }
        }
        return routerDao.save(router);
    }
    private byte[] setAttribute(String name,Router source,Router target,int len){
        byte[] result = null;
        try {
            String sourceData = SpringContextUtil.getSourceData(name, source);
            String targetData = SpringContextUtil.getSourceData(name, target);
            if(targetData!=null  && !targetData.equals(sourceData)){
                result = SpringContextUtil.int2ByteArr(Integer.valueOf(targetData), len);
            }
            else{
                result = new byte[len];
                for(int i=0;i<len;i++)
                    result[i] = (byte) 0xff;
            }
        }
        catch (Exception e){}
        return result;
    }
}

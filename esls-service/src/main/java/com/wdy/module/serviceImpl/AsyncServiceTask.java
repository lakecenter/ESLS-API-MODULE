package com.wdy.module.serviceImpl;

import com.google.common.collect.Lists;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.SuccessAndFailList;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.*;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.entity.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * @author dongyang_wu
 */
@Slf4j
@Component("AsyncServiceTask")
public class AsyncServiceTask {
    @Autowired
    private NettyUtil nettyUtil;
    @Autowired
    private TagService tagService;
    @Autowired
    private StyleService styleService;
    @Autowired
    private GoodService goodService;

    @Async
    public ListenableFuture<Integer> sendMessageWithRepeat(List<Tag> tagList, long begin, String contentType, Integer messageType, Integer depth) throws ExecutionException, InterruptedException {
        if (depth == 0)
            return new AsyncResult<>(0);
        log.info("-----向(标签集合)发送命令线程-----");
        SuccessAndFailList successAndFailList = null;
        if (tagList.size() > 1) {
            try {
                nettyUtil.awakeFirst(tagList);
            } catch (Exception e) {
            }
            try {
                successAndFailList = sendByTags(tagList, begin, contentType, messageType, 1000);
            } catch (Exception e) {
            }
            try {
                nettyUtil.awakeOverLast(tagList);
            } catch (Exception e) {
            }
        } else {
            successAndFailList = sendByTags(tagList, begin, contentType, messageType, 5500);
        }
        if (successAndFailList == null)
            return new AsyncResult<>(0);
        ListenableFuture<Integer> integerListenableFuture = sendMessageWithRepeat(successAndFailList.getNoSuccessTags(), begin, contentType, messageType, --depth);
        return new AsyncResult<>(integerListenableFuture.get() + successAndFailList.getSuccessNumber());
    }

    @Async
    public ListenableFuture<Integer> updateTagStyle(List<Tag> tagList, long begin, int depth) throws ExecutionException, InterruptedException {
        if (depth == 0 || tagList.size() == 0)
            return new AsyncResult<>(0);
        log.info("-----向(标签集合)发送更新样式命令线程-----");
        SuccessAndFailList successAndFailList = null;
        Integer tagsLengthCommand = Integer.valueOf(SystemVersionArgs.tagsLengthCommand);
        if (tagList.size() > 1) {
            List<List<Tag>> splitByTags = Lists.partition(tagList, tagsLengthCommand);
            for (List tags : splitByTags) {
                try {
                    nettyUtil.awakeFirst(tags);
                } catch (Exception e) {
                }
                try {
                    successAndFailList = updateStylesByTags(tags, begin, false);
                } catch (Exception e) {
                }
                try {
                    nettyUtil.awakeOverLast(tags);
                } catch (Exception e) {
                }
            }
        } else {
            successAndFailList = updateStylesByTags(tagList, begin, true);
        }
        if (successAndFailList == null) {
            return new AsyncResult<>(0);
        }
        ListenableFuture<Integer> integerListenableFuture = updateTagStyle(successAndFailList.getNoSuccessTags(), begin, --depth);
        return new AsyncResult<>(integerListenableFuture.get() + successAndFailList.getSuccessNumber());
    }

    @Async
    public ListenableFuture<Integer> sendMessageWithRepeat(Channel channel, Router router, String contentType, Integer messageType, long begin, int time) {
        log.info("-----向(路由器集合)发送命令线程-----");
        byte[] content = CommandConstant.COMMAND_BYTE.get(contentType);
        byte[] message = CommandConstant.getBytesByType(null, content, messageType);
        String result = nettyUtil.sendMessageWithRepeat(channel, message, time, 5100);
        int sucessNumber = 0;
        if ("成功".equals(result)) {
            TagAndRouterUtil.judgeResultAndSettingRouter(result, begin, router, message, contentType);
            sucessNumber = 1;
        }
        return new AsyncResult<>(sucessNumber);
    }

    @Async
    public ListenableFuture<Integer> sendMessageWithRepeat(Channel channel, byte[] message, Router router, long begin, int time) {
        log.info("-----向(路由器集合)发送命令线程-----");
//        if(message[4]==0  && message[5]==0  && message[5]==0 && message[6]==0)
//            time = 2;
        String result = nettyUtil.sendMessageWithRepeat(channel, message, time, 5100);
        int sucessNumber = 0;
        if ("成功".equals(result)) {
            TagAndRouterUtil.judgeResultAndSettingRouter(result, begin, router, message);
            sucessNumber = 1;
        }
        return new AsyncResult<>(sucessNumber);
    }

    private SuccessAndFailList sendByTags(List<Tag> tagList, long begin, String contentType, Integer messageType, Integer commandWaitingTime) {
        int successNumber = 0;
        byte[] content = CommandConstant.COMMAND_BYTE.get(contentType);
        List<Tag> nosuccessTags = new ArrayList<>();
        for (Tag tag : tagList) {
            String result = "失败";
            Channel channel = SocketChannelHelper.getChannelByRouter(tag.getRouter().getId());
            byte[] address = SpringContextUtil.getAddressByBarCode(tag.getBarCode());
            if (channel == null || address == null || (tag.getForbidState() != null && tag.getForbidState() == 0)) {
                TagAndRouterUtil.judgeResultAndSettingTag(result, contentType, begin, tag);
                continue;
            }
            byte[] message = CommandConstant.getBytesByType(address, content, messageType);
            try {
                result = nettyUtil.sendMessageWithRepeat(channel, message, Integer.valueOf(SystemVersionArgs.commandRepeatTime), commandWaitingTime);
            } catch (Exception e) {
                TagAndRouterUtil.judgeResultAndSettingTag(result, contentType, begin, tag);
            }
            TagAndRouterUtil.judgeResultAndSettingTag(result, contentType, begin, tag);
            if ("成功".equals(result)) {
                successNumber++;
            } else {
                nosuccessTags.add(tag);
            }
        }
        return new SuccessAndFailList(successNumber, nosuccessTags, null);
    }

    private SuccessAndFailList updateStylesByTags(List<Tag> tagList, long begin, boolean isWaitingLong) {
        int successNumber = 0;
        List<Tag> nosuccessTags = new ArrayList<>();
        List<Tag> successTags = new ArrayList<>();
        for (Tag tag : tagList) {
            ResponseBean responseBean = new ResponseBean(0, 0);
            try {
                responseBean = tagService.updateTagStyle(tag, isWaitingLong);
            } catch (Exception e) {
                if (!e.getMessage().equals(ResultEnum.COMMUNITICATION_ERROR.getMessage())) {
                    responseBean.setSum(1);
                    responseBean.setSuccessNumber(1);
                }
            }
            String result = responseBean.getSuccessNumber() == 1 && responseBean.getSum() == 1 ? "成功" : "失败";
            TagAndRouterUtil.judgeResultAndSettingTagWaitUpdate(result, begin, tag);
            if ("失败".equals(result))
                nosuccessTags.add(tag);
            else
                successTags.add(tag);
            successNumber += responseBean.getSuccessNumber();
        }
        return new SuccessAndFailList(successNumber, nosuccessTags, successTags);
    }

    @Async
    public ListenableFuture<Integer> sendBindTagGood(Tag tag, Good good, Integer mode) {
        ResponseBean responseBean;
        String contentType;
        // 换绑
        switch (mode) {
            case 2:
                responseBean = tagService.updateTagStyle(tag, good, good.getRegionNames(), true);
                if (responseBean.getSuccessNumber() == 1) {
                    // 换绑
                    setGoodTagBind(good, tag);
                }
                break;
            case 1:
                contentType = CommandConstant.TAGBIND;
                responseBean = SendCommandUtil.sendCommandWithTags(Arrays.asList(tag), contentType, CommandConstant.COMMANDTYPE_TAG, true);
                if (responseBean.getSuccessNumber() == 1) {
                    // 绑定
                    setGoodTagBind(good, tag);
                    tagService.updateTagStyle(tag, good, good.getRegionNames(), true);
                }
                break;
            case 0:
                contentType = CommandConstant.TAGBINDOVER;
                responseBean = SendCommandUtil.sendCommandWithTags(Arrays.asList(tag), contentType, CommandConstant.COMMANDTYPE_TAG, true);
                if (responseBean.getSuccessNumber() == 1) {
                    setGoodTagUnBind(good, tag);
                }
                break;
            default:
                break;
        }
        return new AsyncResult<>(1);
    }

    @Async
    public ListenableFuture<Integer> sendTagChangeStyle(Tag tag, Style style, Boolean isWaitingLong) {
        ResponseBean responseBean = tagService.updateTagStyle(tag, style.getStyleNumber(), isWaitingLong);
        if (responseBean.getSuccessNumber() == 1) {
            tag.setStyle(style);
            tagService.saveOne(tag);
        }
        return new AsyncResult<>(1);
    }

    public void setGoodTagBind(Good good, Tag tag) {
        Style style = styleService.findByStyleNumberAndIsPromote(tag.getStyle().getStyleNumber(), good.getIsPromote());
        tag.setStyle(style);
        tag.setState((byte) 1);
        tag.setGood(good);
        tag.setState((byte) 1);
        tagService.saveOne(tag);

        // regionNames置空
        good.setRegionNames(null);
        good.setWaitUpdate(1);
        goodService.save(good);
    }

    public void setGoodTagUnBind(Good good, Tag tag) {
        if (tag.getStyle() != null) {
            Style style = styleService.findByStyleNumberAndIsPromote(tag.getStyle().getStyleNumber(), (byte) 0);
            tag.setStyle(style);
        }
        good.setWaitUpdate(1);
        // regionNames置空
        good.setRegionNames(null);
        goodService.save(good);
        tag.setState((byte) 0);
        tag.setWaitUpdate(1);
        tag.setGood(null);
        tagService.saveOne(tag);
    }
}

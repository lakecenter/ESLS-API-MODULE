package com.wdy.module.serviceImpl;

import com.google.common.collect.Lists;
import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.response.SuccessAndFailList;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.serviceUtil.*;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.MailSender;
import com.wdy.module.entity.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import com.wdy.module.service.TagService;

import java.util.ArrayList;
import java.util.List;
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
    private MailSender mailSender;

    @Async
    public ListenableFuture<Integer> sendMessageWithRepeat(List<Tag> tagList, long begin, byte[] content, Integer messageType, Integer depth) throws ExecutionException, InterruptedException {
        if (depth == 0)
            return new AsyncResult<>(0);
        log.info("-----向(标签集合)发送命令线程-----");
        SuccessAndFailList successAndFailList = null;
        if (tagList.size() > 1) {
            nettyUtil.awakeFirst(tagList);
            try {
                successAndFailList = sendByTags(tagList, begin, content, messageType, 1000);
            } catch (Exception e) {
            }
            try {
                nettyUtil.awakeOverLast(tagList);
            } catch (Exception e) {
            }
        } else {
            successAndFailList = sendByTags(tagList, begin, content, messageType, 5500);
        }
        if (successAndFailList == null)
            return new AsyncResult<>(0);
        ListenableFuture<Integer> integerListenableFuture = sendMessageWithRepeat(successAndFailList.getNoSuccessTags(), begin, content, messageType, --depth);
        return new AsyncResult<>(integerListenableFuture.get() + successAndFailList.getSuccessNumber());
    }

    @Async
    public ListenableFuture<Integer> updateTagStyle(List<Tag> tagList, List<Tag> tagsAll, long begin, int depth, boolean isNeedSending) throws ExecutionException, InterruptedException {
        if (depth == 0 || tagList.size() == 0)
            return new AsyncResult<>(0);
        log.info("-----向(标签集合)发送更新样式命令线程-----");
        SuccessAndFailList successAndFailList = null;
        Integer tagsLengthCommand = Integer.valueOf(SystemVersionArgs.tagsLengthCommand);
        if (tagList.size() > 1) {
            List<List<Tag>> splitByTags = Lists.partition(tagList, tagsLengthCommand);
            for (List tags : splitByTags) {
                nettyUtil.awakeFirst(tags);
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
            List<List<Tag>> splitByTags = Lists.partition(tagList, tagsLengthCommand);
            for (List tags : splitByTags)
                successAndFailList = updateStylesByTags(tags, begin, true);
        }
        if (successAndFailList == null)
            return new AsyncResult<>(0);
        if (depth == 1 && isNeedSending) {
            List<Tag> noSuccessTags = successAndFailList.getNoSuccessTags();
            try {
                List<User> tos = MessageSender.getUsersByTags(tagsAll);
                StringBuffer sb = new StringBuffer();
                sb.append("总数:" + tagsAll.toString()).append("失败:" + noSuccessTags.toString());
                for (User user : tos) {
                    mailSender.sendMail(user.getMail(), "ESLS变价通知信息邮件", sb.toString(), false);
                }
            } catch (Exception e) {
                log.info("发送ESLS变价通知信息邮件失败" + e);
            }
        }
        ListenableFuture<Integer> integerListenableFuture = updateTagStyle(successAndFailList.getNoSuccessTags(), tagsAll, begin, --depth, isNeedSending);
        return new AsyncResult<>(integerListenableFuture.get() + successAndFailList.getSuccessNumber());
    }

    @Async
    public ListenableFuture<Integer> sendMessageWithRepeat(Channel channel, byte[] message, Router router, long begin, int time) {
        log.info("-----向(路由器集合)发送命令线程-----");
//        if(message[4]==0  && message[5]==0  && message[5]==0 && message[6]==0)
//            time = 2;
        String result = nettyUtil.sendMessageWithRepeat(channel, message, time, 5100);
        int sucessNumber = 0;
        if ("成功".equals(result)) {
            TagUtil.judgeResultAndSettingRouter(result, begin, router, message);
            sucessNumber = 1;
        }
        return new AsyncResult<>(sucessNumber);
    }

    private SuccessAndFailList sendByTags(List<Tag> tagList, long begin, byte[] content, Integer messageType, Integer commandWaitingTime) {
        int successNumber = 0;
        List<Tag> nosuccessTags = new ArrayList<>();
        for (Tag tag : tagList) {
            if (tag.getForbidState() == 0) continue;
            Channel channel = SocketChannelHelper.getChannelByRouter(tag.getRouter().getId());
            if (channel == null) continue;
            byte[] address = SpringContextUtil.getAddressByBarCode(tag.getBarCode());
            if (address == null || (tag.getForbidState() != null && tag.getForbidState() == 0)) continue;
            byte[] message = CommandConstant.getBytesByType(address, content, messageType);
            String result = "失败";
            try {
                result = nettyUtil.sendMessageWithRepeat(channel, message, Integer.valueOf(SystemVersionArgs.commandRepeatTime), commandWaitingTime);
            } catch (Exception e) {
                TagUtil.judgeResultAndSettingTag(result, begin, tag);
            }
            TagUtil.judgeResultAndSettingTag(result, begin, tag);
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
            } catch (ServiceException e) {
                if (e.getMessage().equals(ResultEnum.TAG_EMPTY_STYLES.getMessage())) {
                    System.out.println("样式为空异常");
                    responseBean.setSuccessNumber(responseBean.getSuccessNumber() + 1);
                }
            }
            String result = responseBean.getSuccessNumber() == 1 ? "成功" : "失败";
            TagUtil.judgeResultAndSettingTagWaitUpdate(result, begin, tag);
            if ("失败".equals(result))
                nosuccessTags.add(tag);
            else
                successTags.add(tag);
            successNumber += responseBean.getSuccessNumber();
        }
        return new SuccessAndFailList(successNumber, nosuccessTags, successTags);
    }

}

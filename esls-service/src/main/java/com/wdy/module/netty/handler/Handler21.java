package com.wdy.module.netty.handler;

import com.wdy.module.entity.*;
import com.wdy.module.netty.command.CommandCategory;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.StyleService;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

@Component("handler21")
@Slf4j
public class Handler21 implements ServiceHandler {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public byte[] executeRequest(byte[] header, byte[] message, Channel channel) {
        log.info("标签注册-----处理器执行！");
        String barCode = null;
        try {
            SpringContextUtil.printBytes("接受标签注册消息包", message);
            barCode = ByteUtil.getDigitalMessage(ByteUtil.splitByte(message, 0, 12));
            String styleNumber = ByteUtil.getDigitalMessage(ByteUtil.splitByte(message, 12, 4));
            String resolutionWidth = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 17, 2));
            String resolutionHeight = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 19, 2));
            String screenType = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 21, 1));
            String power = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 22, 1));
            String tag_rssi = String.valueOf(Integer.valueOf(ByteUtil.getRealMessage(ByteUtil.splitByte(message, 23, 1))) - 256);
            String ap_rssi = String.valueOf(Integer.valueOf(ByteUtil.getRealMessage(ByteUtil.splitByte(message, 24, 1))) - 256);
            String hardVersion = ByteUtil.getVersionMessage(ByteUtil.splitByte(message, 25, 6));
            String softVersion = ByteUtil.getVersionMessage(ByteUtil.splitByte(message, 31, 6));
            System.out.println("条码：" + barCode);
            System.out.println("样式数字：" + styleNumber);
            System.out.println("分辨率宽（长边）：" + resolutionWidth);
            System.out.println("分辨率高（短边）：" + resolutionHeight);
            // 2：黑白屏 ； 3：三色屏
            System.out.println("屏幕类型：" + screenType);
            System.out.println("电量：" + power);
            System.out.println("tagrssi：" + tag_rssi);
            System.out.println("aprssi：" + ap_rssi);
            System.out.println("hardversion：" + hardVersion);
            System.out.println("softversion：" + softVersion);
            TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
            String tagAddress = ByteUtil.getMergeMessage(SpringContextUtil.getAddressByBarCode(barCode));
            System.out.println("标签地址：" + tagAddress);
            Tag tagByTagAddress = tagService.findByTagAddress(tagAddress);
            Tag tag = tagByTagAddress == null ? new Tag() : tagByTagAddress;
            tag.setTagAddress(tagAddress);
            tag.setBarCode(barCode);
            tag.setPower(power + "%");
            tag.setTagRssi(tag_rssi);
            tag.setApRssi(ap_rssi);
            tag.setHardwareVersion(hardVersion);
            tag.setSoftwareVersion(softVersion);
            // 不等待变价
            tag.setWaitUpdate(1);
            // 1启用
            tag.setForbidState(1);
            // 没有绑定
            tag.setState((byte) 0);
            // 已经工作
            tag.setIsWorking((byte) 1);
            if ("2".equals(screenType))
                tag.setScreenType("黑白屏");
            else
                tag.setScreenType("三色屏");
            tag.setResolutionWidth(resolutionWidth);
            tag.setResolutionHeight(resolutionHeight);
            tag.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            // 绑定路由器
            Router router = SocketChannelHelper.getRouterByChannel(channel);
            System.out.println("选择的路由器:" + router);
            tag.setRouter(router);
            // 找到标签对应的样式
            StyleService styleService = ((StyleService) SpringContextUtil.getBean("StyleService"));
            List<Style> style = styleService.findByStyleNumber(styleNumber);
            if (!CollectionUtils.isEmpty(style))
                tag.setStyle(style.get(0));
            else {
                TagAndRouterUtil.setBaseTagStyle(Arrays.asList(tag));
            }
            tagService.saveOne(tag);
        } catch (Exception e) {
            System.out.println(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CommandCategory.getResponse(null, header, CommandConstant.COMMANDTYPE_TAG, SpringContextUtil.getAddressByBarCode(barCode), CommandCategory.NACK);
        }
        return CommandCategory.getResponse(null, header, CommandConstant.COMMANDTYPE_TAG, SpringContextUtil.getAddressByBarCode(barCode), CommandCategory.ACK);
    }

}

package com.wdy.module.netty.handler;

import com.wdy.module.entity.Router;
import com.wdy.module.entity.Tag;
import com.wdy.module.service.TagService;
import com.wdy.module.serviceUtil.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component("handler51")
@Slf4j
public class Handler51 implements ServiceHandler {


    @Override
    public byte[] executeRequest(byte[] header, byte[] message, Channel channel) {
        log.info("标签巡检（应答包）-----处理器执行！");

        String tagAddress = ByteUtil.getTagAddress(ByteUtil.splitByte(message, 0, 4));
        String power = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 4, 1));
        String tag_rssi = String.valueOf(Integer.valueOf(ByteUtil.getRealMessage(ByteUtil.splitByte(message, 5, 1)))-256);
        String ap_rssi = String.valueOf(Integer.valueOf(ByteUtil.getRealMessage(ByteUtil.splitByte(message, 6, 1)))-256);
        byte[] state = ByteUtil.splitByte(message, 7, 1);
        System.out.println("标签地址："+tagAddress);
        System.out.println("电量："+power);
        System.out.println("tagrssi："+tag_rssi);
        System.out.println("aprssi："+ap_rssi);
        System.out.println("state："+state[0]);
        Router router = SocketChannelHelper.getRouterByChannel(channel);
        System.out.println("选择的路由器:"+router);
        TagService tagService = ((TagService) SpringContextUtil.getBean("TagService"));
        Tag tag  = tagService.findByTagAddress(tagAddress);
        tag = tag==null?new Tag():tag;
        if(tag!=null){
            tag.setTagAddress(tagAddress);
            tag.setPower(power+"%");
            tag.setTagRssi(tag_rssi);
            tag.setApRssi(ap_rssi);
            if(state[0]==0) {
                tag.setGood(null);
                tag.setState(state[0]);
            }
            tag.setForbidState(1);
            tag.setIsWorking((byte) 1);
            tag.setRouter(router);
            tag.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            tagService.saveOne(tag);
        }
        return null;
        //return CommandCategory.getResponse(null,header,CommandConstant.COMMANDTYPE_TAG_BROADCAST,null);
    }
}

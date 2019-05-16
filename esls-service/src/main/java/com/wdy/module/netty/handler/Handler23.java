package com.wdy.module.netty.handler;

import com.wdy.module.entity.Router;
import com.wdy.module.netty.command.CommandCategory;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.service.RouterService;
import com.wdy.module.serviceUtil.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.sql.Timestamp;

@Component("handler23")
@Slf4j
public class Handler23 implements ServiceHandler {
    @Override
    public byte[] executeRequest(byte[] header, byte[] message, Channel channel) {
        log.info("路由器注册（更新）-----处理器执行！");
        try {
            String routerMac = ByteUtil.getMergeMessage(ByteUtil.splitByte(message, 0, 6));
            String routerIP = ByteUtil.getIpMessage(ByteUtil.splitByte(message, 6, 4));
            String routerBarCode = ByteUtil.getDigitalMessage(ByteUtil.splitByte(message, 10, 12));
            String routerChannelId = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 22, 1));
            String routerFrequency = ByteUtil.getRealMessage(ByteUtil.splitByte(message, 23, 2)) + "0000";
            String routerHardVersion = ByteUtil.getVersionMessage(ByteUtil.splitByte(message, 25, 6));
            String routerSoftVersion = ByteUtil.getVersionMessage(ByteUtil.splitByte(message, 31, 6));
            RouterService routerService = ((RouterService) SpringContextUtil.getBean("RouterService"));
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            String ip = socketAddress.getAddress().getHostAddress();
            Router router = routerService.findByBarCode(routerBarCode);
            if (!SocketChannelHelper.channelIdGroup.containsKey(ip + socketAddress.getPort()))
                SocketChannelHelper.channelIdGroup.put(ip + socketAddress.getPort(), channel);
            // 为空则新增，否则更新
            Router r = router == null ? new Router() : router;
            r.setMac(routerMac);
            r.setIp(routerIP);
            r.setOutNetIp(ip);
            r.setPort(socketAddress.getPort());
            r.setBarCode(routerBarCode);
            r.setChannelId(routerChannelId);
            r.setSoftVersion(routerSoftVersion);
            r.setHardVersion(routerHardVersion);
            r.setFrequency(routerFrequency);
            r.setIsWorking((byte) 1);
            r.setState((byte) 1);
            r.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            routerService.saveOne(r);
        } catch (Exception e) {
            return CommandCategory.getResponse(null, header, CommandConstant.COMMANDTYPE_ROUTER, null, CommandCategory.NACK);
        }
        return CommandCategory.getResponse(null, header, CommandConstant.COMMANDTYPE_ROUTER, null, CommandCategory.ACK);
    }
}

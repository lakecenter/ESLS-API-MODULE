package com.wdy.module.serviceUtil;

import com.wdy.module.entity.Router;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.wdy.module.service.RouterService;

import java.net.InetSocketAddress;
import java.util.*;

@Slf4j
@Component("SocketChannelHelper")
public class SocketChannelHelper {
    public static Map<String, String> rssiResponse = new HashMap<>();
    public static Set<String> ipHistory = new LinkedHashSet<>();
    public static Map<String, Channel> channelIdGroup = new HashMap<>();
    private static List<String> workingChannel = new ArrayList<>();
    public static Map<String, Integer> heartBeanMap = new HashMap<>();


    public static synchronized void addWorkingChannel(String channelId) {
        workingChannel.add(channelId);
    }

    public static synchronized void removeWorkingChannel(String channelId) {
        workingChannel.remove(channelId);
    }

    public static synchronized boolean isWorking(String channelId) {
        return workingChannel.contains(channelId);
    }

    public static synchronized Channel getChannelByRouter(Router router) {
        Channel channel = channelIdGroup.get(router.getOutNetIp() + router.getPort());
        return channel;
    }

    public static synchronized Channel getChannelByRouter(Long routerId) {
        RouterService routerService = (RouterService) SpringContextUtil.getBean("RouterService");
        Router router = routerService.findById(routerId).get();
        Channel channel = channelIdGroup.get(router.getOutNetIp() + router.getPort());
        return channel;
    }

    public static synchronized Router getRouterByChannel(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        Router router = ((RouterService) SpringContextUtil.getBean("RouterService")).findByOutNetIpAndPort(socketAddress.getAddress().getHostAddress(), socketAddress.getPort());
        return router;
    }

    // 通讯响应Map管理
    public static Map<String, ChannelPromise> promiseMap = new HashMap<>();
    public static Map<String, String> dataMap = new HashMap<>();

    public static String getData(Channel channel, byte[] message) {
        String key = getSendKeyByChannelId(channel.id().toString(), message);
        return dataMap.get(key);
    }

    public static synchronized void removeMapWithKey(Channel channel, byte[] message) {
        String key = getSendKeyByChannelId(channel.id().toString(), message);
        dataMap.remove(key);
        promiseMap.remove(key);
    }

    public static synchronized String getSendKeyByChannelId(String channelId, byte[] message) {
        String key = channelId + "-" + message[8] + message[9];
        return key;
    }

    public static synchronized String getMapSize() {
        return "dataMap--" + dataMap.size() + " " + dataMap.toString() + "  promiseMap--" + promiseMap.size() + " " + promiseMap.toString();
    }

    public static boolean isBroadcastCommand(byte[] message) {
        // 返回false则需要等待应答
        // 路由器升级文件需要应答
        if (!((message[0] == 0x11 && message[1] == 0x11) || (message[0] == 0x22 && message[1] == 0x22)))
            return false;
        // 标签广播
        if (message[4] == 0x00 && message[5] == 0x00 && message[6] == 0x00 && message[7] == 0x00)
            return true;
        // 路由器设置 和 结束唤醒 当作普通命令 通讯超时当做广播命令
        if ((message[8] == 0x01 && message[9] == 0x03))
            return true;
        else if ((message[4] == (byte) 0xff && message[5] == (byte) 0xff && message[6] == (byte) 0xff && message[7] == (byte) 0xff) && message[8] == 4 && message[9] == 7)
            return false;
        else if ((message[4] == (byte) 0xff && message[5] == (byte) 0xff && message[6] == (byte) 0xff && message[7] == (byte) 0xff) && message[8] == 2 && message[9] == 5)
            return false;
            // 路由器测试
        else if (message[8] == 9)
            return false;
            // 路由器IP设置
        else if (message[8] == 0x0A)
            return false;
            // 在线升级命令
        else if (message[8] == 0x0c)
            return false;
            // 路由器命令
        else if (message[4] == (byte) 0xff && message[5] == (byte) 0xff && message[6] == (byte) 0xff && message[7] == (byte) 0xff)
            return true;
        return false;
    }

    public static void initRssiMap() {
        rssiResponse = new HashMap<>();
    }
}

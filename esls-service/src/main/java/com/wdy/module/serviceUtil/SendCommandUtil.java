package com.wdy.module.serviceUtil;

import com.wdy.module.common.exception.ResultEnum;
import com.wdy.module.common.response.ResponseBean;
import com.wdy.module.common.exception.ServiceException;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.dto.TagsAndRouter;
import com.wdy.module.entity.*;
import com.wdy.module.service.GoodService;
import com.wdy.module.system.SystemVersionArgs;
import com.wdy.module.utils.*;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import com.wdy.module.service.RouterService;
import com.wdy.module.serviceImpl.AsyncServiceTask;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SendCommandUtil {
    public static ResponseBean sendCommandWithRouters(List<Router> routers, String contentType, Integer messageType) {
        int sum = routers.size();
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        byte[] content = CommandConstant.COMMAND_BYTE.get(contentType);
        byte[] message = CommandConstant.getBytesByType(null, content, messageType);
        try {
            for (Router router : routers) {
                // 路由器未连接或禁用
                if (router.getState() != null && router.getState() == 0) continue;
                Channel channel = SocketChannelHelper.getChannelByRouter(router);
                if (channel == null) continue;
                // 广播命令只发一次 广播命令没有响应
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, message, router, System.currentTimeMillis(), 1);
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            log.info("SendCommandUtil - sendCommandWithRouters : " + e);
        }
        return new ResponseBean(sum, sum);
    }

    public static ResponseBean sendCommandWithRouters(List<Router> routers, byte[] message) {
        int sum = routers.size();
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
        for (Router router : routers) {
            // 路由器未连接或禁用
            if (router.getState() != null && router.getState() == 0) continue;
            Channel channel = SocketChannelHelper.getChannelByRouter(router);
            if (channel == null) continue;
            // 广播命令只发一次 广播命令没有响应
            ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), 1);
            listenableFutures.add(result);
        }
        return new ResponseBean(sum, sum);
    }

    public static ResponseBean sendCommandWithRoutersUpdate(List<Router> routers, MultipartFile file) throws IOException {
        int sum = routers.size();
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        int ack;
        Router router = routers.get(0);
        // 路由器未连接或禁用
        Channel channel = SocketChannelHelper.getChannelByRouter(router);
        if (channel == null)
            throw new ServiceException(ResultEnum.COMMUNITICATION_ERROR);
        // 广播命令只发一次 广播命令没有响应
        // 发送升级包大小
        long length = file.getSize();
        byte[] content = new byte[7];
        content[0] = 0x0c;
        content[1] = 0x01;
        content[2] = 4;
        byte[] lengthByte = SpringContextUtil.int2ByteArr((int) length, 4);
        content[3] = lengthByte[0];
        content[4] = lengthByte[1];
        content[5] = lengthByte[2];
        content[6] = lengthByte[3];
        byte[] realMessage = CommandConstant.getBytesByType(null, content, CommandConstant.COMMANDTYPE_ROUTER);
        ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), 1);
        listenableFutures.add(result);
        ack = waitAllThread(listenableFutures);
        listenableFutures = new ArrayList<>();
        if (ack == 1) {
            realMessage = CommandConstant.COMMAND_BYTE.get(CommandConstant.ROUTER_UPDATE_BEGIN);
            result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), 1);
            listenableFutures.add(result);
            ack = waitAllThread(listenableFutures);
        }
        listenableFutures = new ArrayList<>();
        if (ack == 1) {
            InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, out.toByteArray(), router, System.currentTimeMillis(), 1);
            listenableFutures.add(result);
            ack = waitAllThread(listenableFutures);
        }
        return new ResponseBean(sum, ack);
    }

    public static ResponseBean sendCommandWithTags(List<Tag> tags, String contentType, Integer messageType) {
        int sum = tags.size();
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            List<TagsAndRouter> tagsAndRouters = TagUtil.splitTagsByRouter(tags);
            byte[] content = CommandConstant.COMMAND_BYTE.get(contentType);
            for (TagsAndRouter tagsAndRouter : tagsAndRouters) {
                ArrayList<Tag> tagsList = tagsAndRouter.getTags();
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(tagsList, System.currentTimeMillis(), content, messageType, 1);
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            log.info("SendCommandUtil - sendCommandWithTags : " + e);
        }
        //等待所有线程执行完在返回
//        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(sum, sum);
    }

    public static ResponseBean sendCommandWithSettingRouters(List<Router> routers) {
        int sum = routers.size();
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            for (Router router : routers) {
                Channel channel = SocketChannelHelper.getChannelByRouter(router);
                if (channel == null) continue;
                if (router.getIsWorking() == 0 || (router.getState() != null && router.getState() == 0)) continue;
                // 更新路由器 发送设置命令
                byte[] message = new byte[16];
                message[0] = 0x02;
                message[1] = 0x05;
                message[2] = 0x0D;
                byte[] mac = ByteUtil.getMacMessage(router.getMac());
                for (int i = 0; i < mac.length; i++)
                    message[3 + i] = mac[i];
                // IP地址
                String ip = router.getIp();
                String[] ips = ip.split("\\.");
                for (int i = 0; i < 4; i++)
                    message[9 + i] = (byte) Integer.parseInt(ips[i]);
                // 信道
                message[13] = Byte.parseByte(router.getChannelId());
                // 频率
                byte[] frequency = SpringContextUtil.int2ByteArr(Integer.valueOf(router.getFrequency()), 2);
                for (int i = 0; i < frequency.length; i++)
                    message[14 + i] = frequency[i];
                SpringContextUtil.printBytes("路由器设置信息：", message);
                byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), Integer.valueOf(SystemVersionArgs.commandRepeatTime));
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //等待所有线程执行完在返回
//        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(sum, sum);
    }

    public static ResponseBean updateTagStyle(List<Tag> tags, boolean isNeedWaiting, boolean isNeedSending) {
        int sum = tags.size(), successNumber = tags.size();
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();

        try {
            List<TagsAndRouter> tagsAndRouters = TagUtil.splitTagsByRouter(tags);
            for (TagsAndRouter tagsAndRouter : tagsAndRouters) {
                if (tagsAndRouter.getRouter() == null) continue;
                testIfValid("UPDATE_TAG_STYLE_" + tagsAndRouter.getRouter().getId() + "_");
                ArrayList<Tag> tagsList = tagsAndRouter.getTags();
                if (CollectionUtils.isEmpty(tagsList))
                    continue;
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).updateTagStyle(tagsList, tagsList, System.currentTimeMillis(), Integer.valueOf(SystemVersionArgs.recursionDepth), isNeedSending);
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            throw new ServiceException(-1, "SendCommandUtil-updateTagStyle-" + e);
        }
        if (isNeedWaiting) {
            successNumber = waitAllThread(listenableFutures);
        }
        // 所有的标签都变价成功，才更新商品状态
        if (sum == successNumber) {
            setGoodWaitUpdate(tags);
        }
        return new ResponseBean(sum, successNumber);
    }

    public static ResponseBean sendAwakeMessage(List<TagsAndRouter> tagsAndRouters, Integer messageType) {
        int successNumber;
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            for (TagsAndRouter tagsAndRouter : tagsAndRouters) {
                Channel channel = SocketChannelHelper.getChannelByRouter(tagsAndRouter.getRouter());
                if (channel == null)
                    continue;
                List<byte[]> byteList = SpringContextUtil.getAwakeBytes(tagsAndRouter.getTags());
                for (byte[] content : byteList) {
                    byte[] message = CommandConstant.getBytesByType(null, content, messageType);
                    ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, message, tagsAndRouter.getRouter(), System.currentTimeMillis(), 1);
                    listenableFutures.add(result);
                    // 判断是否成功
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //等待所有线程执行完在返回
        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(0, successNumber);
    }

    public static int waitAllThread(ArrayList<ListenableFuture<Integer>> listenableFutures) {
        ArrayList<Integer> listenableFuturesResults = new ArrayList<>();
        //等待所有线程执行完在返回
        int sumBreak = 0, sumThreads = listenableFutures.size(), successNumber = 0;
        while (true) {
            //遍历所有线程 获得结果
            for (int i = 0; i < listenableFutures.size(); i++) {
                ListenableFuture<Integer> item = listenableFutures.get(i);
                try {
                    if (item.isDone()) {
                        sumBreak++;
                        listenableFutures.remove(i);
                        log.info(item.toString() + "最终响应结果:" + item.get());
                        if (item.get() > 0) {
                            listenableFuturesResults.add(item.get());
                            successNumber += item.get();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            if (sumBreak == sumThreads)
                break;
        }
        return successNumber;
    }

    // 路由器测试
    // AP写入
    public static ResponseBean sendAPWrite(List<Router> routers, String barCode, String channelId, String hardVersion) {
        int sum = routers.size(), successNumber;
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            for (Router router : routers) {
                Channel channel = SocketChannelHelper.getChannelByRouter(router);
                if (channel == null) continue;
                if (router.getIsWorking() == 0 || (router.getState() != null && router.getState() == 0)) continue;
                // 更新路由器 发送设置命令
                byte[] message = new byte[22];
                message[0] = 0x09;
                message[1] = 0x02;
                message[2] = 0x13;
                // 条码
                for (int i = 0; i < barCode.length(); i++)
                    message[3 + i] = (byte) barCode.charAt(i);
                // 通道号
                message[15] = Byte.parseByte(channelId);
                // 硬件版本号
                byte[] versionMessage = ByteUtil.getVersionMessage(hardVersion);
                for (int i = 0; i < versionMessage.length; i++)
                    message[16 + i] = versionMessage[i];
                RouterService routerService = (RouterService) SpringContextUtil.getBean("RouterService");
                router.setBarCode(barCode);
                router.setChannelId(channelId);
                router.setHardVersion(hardVersion);
                routerService.saveOne(router);
                SpringContextUtil.printBytes("AP写入信息：", message);
                byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), Integer.valueOf(SystemVersionArgs.commandRepeatTime));
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            log.error("sendAPWrite:" + e);
        }
        //等待所有线程执行完在返回
        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(sum, successNumber);
    }

    // AP发送无线帧
    public static ResponseBean sendAPByChannelId(List<Router> routers, String channelId) {
        int sum = routers.size(), successNumber;
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            for (Router router : routers) {
                Channel channel = SocketChannelHelper.getChannelByRouter(router);
                if (channel == null) continue;
                if (router.getIsWorking() == 0 || (router.getState() != null && router.getState() == 0)) continue;
                // 更新路由器 发送设置命令
                byte[] message = new byte[4];
                message[0] = 0x09;
                message[1] = 0x6;
                message[2] = 0x01;
                message[3] = Byte.parseByte(channelId);
                SpringContextUtil.printBytes("AP发送无线帧：", message);
                byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), Integer.valueOf(SystemVersionArgs.commandRepeatTime));
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            log.error("sendAPByChannelId:" + e);
        }
        //等待所有线程执行完在返回
        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(sum, successNumber);
    }

    // AP接收无线帧
    public static ResponseBean sendAPReceiveByChannelId(List<Router> routers, String channelId) {
        int sum = routers.size(), successNumber;
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            for (Router router : routers) {
                Channel channel = SocketChannelHelper.getChannelByRouter(router);
                if (channel == null) continue;
                if (router.getIsWorking() == 0 || (router.getState() != null && router.getState() == 0)) continue;
                // 更新路由器 发送设置命令
                byte[] message = new byte[4];
                message[0] = 0x09;
                message[1] = 0x7;
                message[2] = 0x01;
                message[3] = Byte.parseByte(channelId);
                SpringContextUtil.printBytes("AP发送接收无线帧：", message);
                byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), Integer.valueOf(SystemVersionArgs.commandRepeatTime));
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            log.error("sendAPReceiveByChannelId:" + e);
        }
        //等待所有线程执行完在返回
        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(sum, successNumber);
    }

    // 设置当前目标服务器IP
    public static ResponseBean setLocalhostIp(List<Router> routers, String outNetIp) {
        int sum = routers.size(), successNumber;
        ArrayList<ListenableFuture<Integer>> listenableFutures = new ArrayList<>();
        try {
            for (Router router : routers) {
                Channel channel = SocketChannelHelper.getChannelByRouter(router);
                if (channel == null) continue;
                if (router.getIsWorking() == 0 || (router.getState() != null && router.getState() == 0)) continue;
                byte[] message = new byte[7];
                message[0] = 0x0A;
                message[1] = 0x01;
                message[2] = 4;
//                SystemVersion systemVersion = systemVersionService.findById((long) 1).get();
//                String outNetIp = systemVersion.getOutNetIp();
                String[] split = outNetIp.split("\\.");
                int a = Integer.parseInt(split[0]);
                int b = Integer.parseInt(split[1]);
                int c = Integer.parseInt(split[2]);
                int d = Integer.parseInt(split[3]);
                message[3] = (byte) a;
                message[4] = (byte) b;
                message[5] = (byte) c;
                message[6] = (byte) d;
                SpringContextUtil.printBytes("设置当前目标服务器IP：", message);
                byte[] realMessage = CommandConstant.getBytesByType(null, message, CommandConstant.COMMANDTYPE_ROUTER);
                ListenableFuture<Integer> result = ((AsyncServiceTask) SpringContextUtil.getBean("AsyncServiceTask")).sendMessageWithRepeat(channel, realMessage, router, System.currentTimeMillis(), 1);
                listenableFutures.add(result);
            }
        } catch (Exception e) {
            log.error("sendAPReceiveByChannelIdStop:" + e);
        }
        //等待所有线程执行完在返回
//        successNumber = waitAllThread(listenableFutures);
        return new ResponseBean(sum, sum);
    }

    public static void testIfValid(String methodName) {
        RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
        String redisCache = (String) redisUtil.sentinelGet(methodName + ContextUtil.getUser().getName(), String.class);
        String timeGapAndtime[] = SystemVersionArgs.timeGapAndTime.split(" ");
        if (timeGapAndtime[1].equals(redisCache)) {
            throw new ServiceException(ResultEnum.STYLE_SEND_WORKING);
        }
        if (StringUtils.isEmpty(redisCache))
            redisCache = "0";
        redisUtil.sentinelSet(methodName + ContextUtil.getUser().getName(), Integer.valueOf(redisCache) + 1, Long.valueOf(Integer.valueOf(timeGapAndtime[0])));
    }

    private static void setGoodWaitUpdate(List<Tag> tags) {
        for (Tag tag : tags) {
            Good good = tag.getGood();
            if (good != null) {
                GoodService goodService = (GoodService) SpringContextUtil.getBean("GoodService");
                good.setWaitUpdate(0);
                good.setRegionNames(null);
                goodService.save(good);
            }
        }
    }
}

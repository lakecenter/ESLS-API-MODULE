package com.wdy.module.netty;

import com.wdy.module.common.constant.TableConstant;
import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;
import com.wdy.module.netty.command.CommandConstant;
import com.wdy.module.netty.executor.AsyncTask;
import com.wdy.module.service.Service;
import com.wdy.module.serviceUtil.ByteUtil;
import com.wdy.module.serviceUtil.SocketChannelHelper;
import com.wdy.module.serviceUtil.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("服务端客户加入连接====>" + ctx.channel().toString());
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        SocketChannelHelper.channelIdGroup.put(socketAddress.getAddress().getHostAddress() + socketAddress.getPort(), ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("服务端客户移除连接====>" + ctx.channel().remoteAddress());
        removeWorkingRouter(ctx.channel());
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        SocketChannelHelper.channelIdGroup.remove(socketAddress.getAddress().getHostAddress() + socketAddress.getPort());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("【系统异常】======>" + cause.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info((new StringBuilder("NettyServerHandler::活跃 remoteAddress=")).append(ctx.channel().remoteAddress()).toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    // 超时处理
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            switch (stateEvent.state()) {
                //读空闲（服务器端）
                case READER_IDLE:
                    String id = ctx.channel().id().toString();
                    SocketChannelHelper.heartBeanMap.put(id, SocketChannelHelper.heartBeanMap.get(id) + 1);
                    // 重试2次无响应则断开
                    if (SocketChannelHelper.heartBeanMap.get(id) + 1 == 2) {
                        removeWorkingRouter(ctx.channel());
                        SocketChannelHelper.heartBeanMap.remove(id);
                        log.info("【" + ctx.channel().remoteAddress() + "】客户端断开");
                    }
                    Thread.sleep(1000L);
                    break;
                //写空闲（客户端）
                case WRITER_IDLE:
                    byte[] heartBeat = CommandConstant.COMMAND_BYTE.get(CommandConstant.ROUTERHEARTBEAN);
                    ctx.channel().writeAndFlush(Unpooled.wrappedBuffer(heartBeat));
                    log.info("【" + ctx.channel().remoteAddress() + "】发送心跳包");
                    break;
                case ALL_IDLE:
                    break;
                default:
                    break;
            }
        }
    }

    // 接受消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] req = new byte[in.readableBytes()];
        in.readBytes(req);
        if (req.length < 3) return;
        byte[] header = new byte[2];
        header[0] = req[8];
        header[1] = req[9];
        String handlerName = "handler" + header[0] + "" + header[1];
//        if (CommandConstant.ACK.equals(CommandCategory.getCommandCategory(header))) {
        // ACK
        if (header[0] == 0x01 && header[1] == 0x01) {
            if (req[11] == 0x0B && req[12] == 0x01) {
                log.info("接收到心跳包");
                return;
            }
            String key = ctx.channel().id().toString() + "-" + req[11] + req[12];
            SpringContextUtil.printBytes("key = " + key + " 接收ACK消息", req);
            if (SocketChannelHelper.promiseMap.containsKey(key)) {
                SocketChannelHelper.dataMap.put(key, "成功");
                SocketChannelHelper.promiseMap.get(key).setSuccess();
            }
        }
        // NACK
        else if (header[0] == 0x01 && header[1] == 0x02) {
            String key = ctx.channel().id().toString() + "-" + req[11] + req[12];
            SpringContextUtil.printBytes("key = " + key + " 接收NACK消息", req);
            SocketChannelHelper.dataMap.put(key, "失败");
            SocketChannelHelper.promiseMap.get(key).setSuccess();
        }
//        // 通讯超时
//        else if (SocketChannelHelper.promiseMap.size()>0 && CommandConstant.OVERTIME.equals(CommandCategory.getCommandCategory(header))) {
//            String key = ctx.channel().id().toString()+"-"+req[11]+req[12]+"-"+SpringContextUtil.toHex(req[13])+SpringContextUtil.toHex(req[14])+SpringContextUtil.toHex(req[15])+SpringContextUtil.toHex(req[16]);
//            SpringContextUtil.printBytes("key = "+key+" 接收通讯超时消息",req);
//            SocketChannelHelper.dataMap.put(key,"通讯超时");
//            SocketChannelHelper.promiseMap.get(key).setSuccess();
//        }
//        else if(CommandConstant.TAGRESPONSE.equals(CommandCategory.getCommandCategory(header))){
        // tag巡检应答包
        else if (header[0] == 0x05 && header[1] == 0x01) {
            byte[] bytes = ByteUtil.splitByte(req, 11, req[10]);
            String key = SocketChannelHelper.getSendKeyByChannelId(ctx.channel().id().toString(), req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute(handlerName, ctx.channel(), header, bytes);
            SpringContextUtil.printBytes("key = " + key + " 接收tag巡检应答包", req);
            if (SocketChannelHelper.promiseMap.containsKey(key)) {
                SocketChannelHelper.dataMap.put(key, "成功");
                SocketChannelHelper.promiseMap.get(key).setSuccess();
            }
        }
//        else if(CommandConstant.ROUTERRESPONSE.equals(CommandCategory.getCommandCategory(header))){
        // router巡检应答包
        else if (header[0] == 0x05 && header[1] == 0x02) {
            String key = SocketChannelHelper.getSendKeyByChannelId(ctx.channel().id().toString(), req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute("handler23", ctx.channel(), header, ByteUtil.splitByte(req, 11, req[10]));
            SpringContextUtil.printBytes("key = " + key + " 接收router巡检应答包", req);
            if (SocketChannelHelper.promiseMap.containsKey(key)) {
                SocketChannelHelper.dataMap.put(key, "成功");
                SocketChannelHelper.promiseMap.get(key).setSuccess();
            }
        }
//        else  if(CommandConstant.APREAD.equals(CommandCategory.getCommandCategory(header))){
        // AP读取应答包
        else if (header[0] == 0x09 && header[1] == 0x12) {
            byte[] bytes = ByteUtil.splitByte(req, 11, req[10]);
            String key = SocketChannelHelper.getSendKeyByChannelId(ctx.channel().id().toString(), req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute(handlerName, ctx.channel(), header, bytes);
            SpringContextUtil.printBytes("key = " + key + " 接收AP读取应答包消息", req);
            SocketChannelHelper.dataMap.put(key, "成功");
            SocketChannelHelper.promiseMap.get(key).setSuccess();
        }
        // 历史连接IP列表
        else if (header[0] == 0x0a && header[1] == 0x03) {
            String key = SocketChannelHelper.getSendKeyByChannelId(ctx.channel().id().toString(), req);
            SpringContextUtil.printBytes("key = " + key + " 接收到查询历史连接IP列表应答包", req);
            String routerIP = ByteUtil.getIpMessage(ByteUtil.splitByte(req, 11, 4));
            SocketChannelHelper.ipHistory.add(routerIP);
            SocketChannelHelper.dataMap.put(key, "成功");
            SocketChannelHelper.promiseMap.get(key).setSuccess();
        }
        // 接收到无线帧RSSI应答包
        else if (header[0] == 0x09 && header[1] == 0x77) {
            String routerRssiNumber = String.valueOf(req[11] + 256);
            String routerRssi = String.valueOf(req[12]);
            SpringContextUtil.printBytes("接收到无线帧RSSI应答包", req);
            SocketChannelHelper.rssiResponse.put(routerRssiNumber, routerRssi);
        }
        // 获取计量数据应答包
        else if (header[0] == 0x08 && header[1] == 0x01) {
            byte[] bytes = ByteUtil.splitByte(req, 15, req[10]);
            String key = SocketChannelHelper.getSendKeyByChannelId(ctx.channel().id().toString(), req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute(handlerName, ctx.channel(), header, bytes);
            SocketChannelHelper.dataMap.put(key, "成功");
            SocketChannelHelper.promiseMap.get(key).setSuccess();
        }
        // 获取电子秤电量应答包
        else if (header[0] == 0x08 && header[1] == 0x04) {
            byte[] bytes = ByteUtil.splitByte(req, 15, req[10]);
            String key = SocketChannelHelper.getSendKeyByChannelId(ctx.channel().id().toString(), req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute(handlerName, ctx.channel(), header, bytes);
            SpringContextUtil.printBytes("key = " + key + " 接收获取电子秤电量应答包消息", req);
            SocketChannelHelper.dataMap.put(key, "成功");
            SocketChannelHelper.promiseMap.get(key).setSuccess();
        }
//        else if(CommandConstant.ROUTERREGISTY.equals(CommandCategory.getCommandCategory(header))){
        // 路由器注册命令
        else if (header[0] == 0x02 && header[1] == 0x03) {
            String key = ctx.channel().id().toString();
            SpringContextUtil.printBytes("key = " + key + " 接收路由器注册消息", req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute(handlerName, ctx.channel(), header, ByteUtil.splitByte(req, 11, req[10]));
        }
//        else if(CommandConstant.TAGREGISTY.equals(CommandCategory.getCommandCategory(header))){
        // 标签注册命令
        else if (header[0] == 0x02 && header[1] == 0x01) {
            String key = ctx.channel().id().toString();
            SpringContextUtil.printBytes("key = " + key + " 接收标签注册消息", req);
            ((AsyncTask) SpringContextUtil.getBean("AsyncTask")).execute(handlerName, ctx.channel(), header, ByteUtil.splitByte(req, 11, req[10]));
        } else {
            String key = ctx.channel().id().toString();
            SpringContextUtil.printBytes("key = " + key + " 接收到其他消息", req);
        }
    }

    // 主动发送命令
    public ChannelPromise sendMessage(Channel channel, byte[] message) {
        if (channel == null)
            throw new IllegalStateException();
        String key = SocketChannelHelper.getSendKeyByChannelId(channel.id().toString(), message);
        SpringContextUtil.printBytes("key = " + key + " 主动发送命令包：", message);
        ChannelPromise promise = channel.writeAndFlush(Unpooled.wrappedBuffer(message)).channel().newPromise();
        if (!SocketChannelHelper.isBroadcastCommand(message))
            SocketChannelHelper.promiseMap.put(key, promise);
        return promise;
    }

    public void removeWorkingRouter(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        RequestBean source = new RequestBean();
        RequestItem itemSource = new RequestItem("ip", socketAddress.getAddress().getHostAddress());
        source.getItems().add(itemSource);
        RequestBean target = new RequestBean();
        RequestItem itemTarget = new RequestItem("isWorking", String.valueOf(0));
        target.getItems().add(itemTarget);
        // 更新记录数
        ((Service) SpringContextUtil.getBean("BaseService")).updateByArrtribute(TableConstant.TABLE_ROUTERS, source, target);
        channel.close();
    }
}
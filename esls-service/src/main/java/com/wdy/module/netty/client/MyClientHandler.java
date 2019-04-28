package com.wdy.module.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<Object> {
    private ChannelHandlerContext ctx;
    private ChannelPromise promise;
    private String data;
    /**
     * 连接上服务器
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端用户加入连接====>"+ctx.channel().toString());
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端用户移除连接====>"+ctx.channel().remoteAddress());
    }

    /**
     * 连接异常   需要关闭相关资源
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("【系统异常】======>"+cause.toString());
    }

    /**
     * 活跃的通道  也可以当作用户连接上客户端进行使用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info((new StringBuilder("客户端用户加入连接 remoteAddress=")).append(ctx.channel().remoteAddress()).toString());
        this.ctx = ctx;
        ctx.fireChannelActive();
    }
    public ChannelPromise sendMessage(ByteBuf message) {
        if (ctx == null)
            throw new IllegalStateException();
        promise = ctx.writeAndFlush(message).channel().newPromise();
        return promise;
    }
    public String getData() {
        return data;
    }

    /**
     * 不活跃的通道  就说明用户失去连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * 这里只要完成 flush
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) {
        log.info("----NettyClient接受应答包----");
        ByteBuf in = (ByteBuf)msg;
        byte[] req = new byte[in.readableBytes()];
        in.readBytes(req);
        this.data = "成功";
        promise.setSuccess();
    }
}
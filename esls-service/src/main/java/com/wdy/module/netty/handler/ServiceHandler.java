package com.wdy.module.netty.handler;

import io.netty.channel.Channel;

public interface ServiceHandler {
    byte[] executeRequest(byte[] header, byte[] message, Channel channel);
}

package com.xxmicloxx.znetworklib;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by ml on 14.08.14.
 */
public class PipelineInitializer {
    public static void initChannel(SocketChannel ch, boolean isNetwork) throws Exception {
        ch.pipeline().addLast(new PacketSlicer());
        ch.pipeline().addLast(new MessageDecoder(isNetwork));

        ch.pipeline().addLast(new LengthEncoder());
        ch.pipeline().addLast(new MessageEncoder(isNetwork));
    }
}

package com.xxmicloxx.znetworklib;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ml on 14.08.14.
 */
public abstract class InboundHandler extends ChannelInboundHandlerAdapter {
    protected Logger log = Logger.getLogger(getClass().getName());

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.log(Level.SEVERE, "Error in channel pipeline", cause);
        ctx.close();
    }
}

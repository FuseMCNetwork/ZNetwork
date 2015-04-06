package com.xxmicloxx.znetworkserver;

import com.xxmicloxx.znetworklib.InboundHandler;
import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.packet.core.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.util.logging.Level;

/**
 * Created by ml on 14.08.14.
 */
public class MessageHandler extends InboundHandler {
    @Getter
    private String networkIdentifier;

    private ZNetworkServer server = ZNetworkServer.getInstance();

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (networkIdentifier != null) {
            server.getChannelMap().remove(networkIdentifier);
            server.getEventMap().removeAll(networkIdentifier);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (networkIdentifier == null && !(msg instanceof HaveNameRequest)) {
            ctx.close();
            return;
        }

        if (msg instanceof HaveNameRequest) {
            handleNameRequest(ctx, (HaveNameRequest) msg);
        }

        if (msg instanceof GeneralRequest) {
            handleGeneralRequest(ctx, (GeneralRequest) msg);
        }

        if (msg instanceof GeneralResult) {
            handleGeneralResult(ctx, (GeneralResult) msg);
        }

        if (msg instanceof RegisterListenerRequest) {
            handleRegisterListener(ctx, (RegisterListenerRequest) msg);
        }

        if (msg instanceof UnregisterListenerRequest) {
            handleUnregisterListener(ctx, (UnregisterListenerRequest) msg);
        }

        if (msg instanceof EmitEventRequest) {
            handleEmitEvent(ctx, (EmitEventRequest) msg);
        }
    }

    private void handleEmitEvent(ChannelHandlerContext ctx, EmitEventRequest request) {
        EventEmittedRequest result = new EventEmittedRequest(request);
        for (MessageHandler handler : server.getEventMap().get(request.getEvent())) {
            handler.writeAndFlush(result);
        }
    }

    private void handleUnregisterListener(ChannelHandlerContext ctx, UnregisterListenerRequest request) {
        server.getEventMap().remove(request.getEvent(), this);
    }

    private void handleRegisterListener(ChannelHandlerContext ctx, RegisterListenerRequest request) {
        server.getEventMap().put(request.getEvent(), this);
    }

    private void handleGeneralResult(ChannelHandlerContext ctx, GeneralResult msg) {
        if (server.getChannelMap().containsKey(msg.getTarget())) {
            // drop
            log.info("Requesting server went offline while waiting for result...");
            return;
        }

        server.getChannelMap().get(msg.getTarget()).writeAndFlush(msg);
    }

    private void handleGeneralRequest(ChannelHandlerContext ctx, GeneralRequest msg) {
        if (!server.getChannelMap().containsKey(msg.getTarget())) {
            GeneralRequestTargetNotFound targetNotFound = new GeneralRequestTargetNotFound(msg);
            ctx.writeAndFlush(targetNotFound);
        }
        server.getChannelMap().get(msg.getTarget()).writeAndFlush(msg);
    }

    private void handleNameRequest(ChannelHandlerContext ctx, HaveNameRequest request) {
        boolean exists = server.getChannelMap().containsKey(request.getDesiredName());

        if (!exists) {
            networkIdentifier = request.getDesiredName();
            server.getChannelMap().put(networkIdentifier, this);
        }

        log.log(Level.INFO, "Getting mame-request for name " + request.getDesiredName() + ". Result: " + !exists);
        HaveNameResult result = new HaveNameResult();
        result.setSuccessful(!exists);
        ctx.writeAndFlush(result);
    }

    public ChannelFuture writeAndFlush(NetworkPacket o) {
        return ctx.writeAndFlush(o);
    }
}

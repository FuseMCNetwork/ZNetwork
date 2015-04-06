package com.xxmicloxx.znetworkplugin;

import com.google.common.util.concurrent.SettableFuture;
import com.xxmicloxx.znetworklib.InboundHandler;
import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.codec.Result;
import com.xxmicloxx.znetworklib.packet.core.*;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

/**
 * Created by ml on 15.08.14.
 */
public class MessageHandler extends InboundHandler {
    private Logger log = Logger.getLogger(MessageHandler.class.getName());

    private ChannelHandlerContext ctx;

    private ZNetworkPlugin instance = ZNetworkPlugin.getInstance();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Requesting my name");
        this.ctx = ctx;
        HaveNameRequest request = new HaveNameRequest();
        request.setDesiredName(System.getProperty("network.name"));
        ctx.writeAndFlush(request);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        instance.setConnectionName(null);
        instance.setMessageHandler(null);
        if (instance.isShouldShutDown()) {
            log.info("Shutting down network connections...");
        } else {
            log.severe("Connection to network closed! Reconnecting!");
            InitialConnector connector = new InitialConnector();
            connector.setRetry(true);
            connector.startConnection();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (instance.getConnectionName() == null && !(msg instanceof HaveNameResult)) {
            return;
        }

        if (msg instanceof HaveNameResult) {
            handleHaveName(ctx, (HaveNameResult) msg);
        }

        if (msg instanceof GeneralRequest) {
            handleGeneralRequest(ctx, (GeneralRequest) msg);
        }

        if (msg instanceof GeneralResult) {
            handleGeneralResult(ctx, (GeneralResult) msg);
        }

        if (msg instanceof EventEmittedRequest) {
            handleEventEmitted((EventEmittedRequest) msg);
        }

        if (msg instanceof GeneralRequestTargetNotFound) {
            handleRequestError((GeneralRequestTargetNotFound) msg);
        }
    }

    private void handleRequestError(GeneralRequestTargetNotFound result) {
        SettableFuture<Result> future = instance.getRequestMap().remove(result.getHandle());
        if (future == null) {
            log.warning("Received a result without having a request!");
            return;
        }
        future.setException(new TargetOfflineException());
    }

    private void handleEventEmitted(EventEmittedRequest msg) {
        for (EventListener listener : instance.getEventMap().get(msg.getEvent())) {
            listener.onEventReceived(msg.getEvent(), msg.getSender(), msg.getData());
        }
    }

    private void handleGeneralResult(ChannelHandlerContext ctx, GeneralResult result) {
        SettableFuture<Result> future = instance.getRequestMap().remove(result.getHandle());
        if (future == null) {
            log.warning("Received a result without having a request!");
            return;
        }
        future.set(result.getResult());
    }

    private void handleGeneralRequest(final ChannelHandlerContext ctx, GeneralRequest request) {
        final GeneralResult result = new GeneralResult();
        result.setHandle(request.getHandle());
        result.setTarget(request.getSender());
        result.setSender(instance.getConnectionName());

        ResultHandler handler = new ResultHandler() {
            @Override
            public void handle(Result packet) {
                result.setResult(packet);
                ctx.writeAndFlush(packet);
            }
        };

        ZNetworkPlugin.getInstance().getNativePlugin().handleRequest(request, handler);
    }

    private void handleHaveName(ChannelHandlerContext ctx, HaveNameResult msg) {
        if (!msg.isSuccessful()) {
            log.severe("ERROR: Name already taken!");
            ZNetworkPlugin.getInstance().shutdown();
            return;
        }

        String name = System.getProperty("network.name");
        instance.setConnectionName(name);
        instance.setMessageHandler(this);

        // events
        sendAllEvents();

        instance.notifyConnected();
        log.info("Connection established! My name is " + name);
    }

    private void sendAllEvents() {
        for (String event : instance.getEventMap().keySet()) {
            RegisterListenerRequest request = new RegisterListenerRequest();
            request.setSender(instance.getConnectionName());
            request.setEvent(event);
            ctx.write(request);
        }

        ctx.flush();
    }

    public void writeAndFlush(NetworkPacket request) {
        ctx.writeAndFlush(request);
    }
}

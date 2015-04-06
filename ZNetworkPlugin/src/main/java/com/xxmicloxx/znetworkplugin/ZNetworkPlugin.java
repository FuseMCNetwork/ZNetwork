package com.xxmicloxx.znetworkplugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.Request;
import com.xxmicloxx.znetworklib.codec.Result;
import com.xxmicloxx.znetworklib.packet.core.EmitEventRequest;
import com.xxmicloxx.znetworklib.packet.core.GeneralRequest;
import com.xxmicloxx.znetworklib.packet.core.RegisterListenerRequest;
import com.xxmicloxx.znetworklib.packet.core.UnregisterListenerRequest;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ml on 15.08.14.
 */
public class ZNetworkPlugin {
    @Getter
    private static ZNetworkPlugin instance;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private String connectionName;

    @Getter(AccessLevel.PACKAGE)
    private boolean shouldShutDown;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private MessageHandler messageHandler;

    @Getter(AccessLevel.PACKAGE)
    private ZNativePlugin nativePlugin;

    @Getter(AccessLevel.PACKAGE)
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Getter(AccessLevel.PACKAGE)
    private Multimap<String, EventListener> eventMap = HashMultimap.create();

    @Getter(AccessLevel.PACKAGE)
    private Map<UUID, SettableFuture<Result>> requestMap = new ConcurrentHashMap<UUID, SettableFuture<Result>>();

    private final Object connectedLock = new Object();

    public ZNetworkPlugin(ZNativePlugin nativePlugin){
        this.nativePlugin = nativePlugin;
        instance = this;
    }

    public void onEnable() {
        InitialConnector connector = new InitialConnector();
        connector.startConnection();
        checkStarted();
    }

    private void checkStarted() {
        if (messageHandler != null) {
            return;
        }

        while (messageHandler == null) {
            synchronized (connectedLock) {
                try {
                    connectedLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isOnline() {
        return messageHandler != null;
    }

    public ListenableFuture<Result> sendRequest(String target, Request data) {
        GeneralRequest request = new GeneralRequest();
        request.setTarget(target);
        request.setRequest(data);
        request.setHandle(UUID.randomUUID());

        checkStarted();

        request.setSender(connectionName);

        SettableFuture<Result> future = SettableFuture.create();

        requestMap.put(request.getHandle(), future);
        messageHandler.writeAndFlush(request);

        return future;
    }

    public void registerEvent(String event, EventListener listener) {
        if (!eventMap.containsKey(event)) {
            sendRegisterEvent(event);
        }

        eventMap.put(event, listener);
    }

    public void sendEvent(String event, NetworkEvent data) {
        EmitEventRequest request = new EmitEventRequest();
        request.setEvent(event);
        request.setData(data);

        checkStarted();
        request.setSender(connectionName);

        messageHandler.writeAndFlush(request);
    }

    public void unregisterEvent(String event, EventListener listener) {
        boolean contained = eventMap.containsKey(event);
        eventMap.remove(event, listener);

        if (contained && !eventMap.containsKey(event)) {
            // we just lost our last subscriber to this event ->
            // unregister it from the network
            sendUnregisterEvent(event);
        }
    }

    private void sendRegisterEvent(String event) {
        RegisterListenerRequest request = new RegisterListenerRequest();
        request.setEvent(event);

        checkStarted();

        request.setSender(connectionName);

        messageHandler.writeAndFlush(request);
    }

    private void sendUnregisterEvent(String event) {
        UnregisterListenerRequest request = new UnregisterListenerRequest();
        request.setEvent(event);

        checkStarted();

        request.setSender(connectionName);

        messageHandler.writeAndFlush(request);
    }

    public void onDisable() {
        shouldShutDown = true;
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    public void shutdown() {
        nativePlugin.shutdown();
    }

    void notifyConnected() {
        synchronized (connectedLock) {
            connectedLock.notifyAll();
        }
    }
}

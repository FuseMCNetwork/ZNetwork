package com.xxmicloxx.znetworkplugin;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.xxmicloxx.znetworklib.PipelineInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Setter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ml on 15.08.14.
 */
public class InitialConnector {
    private Logger log = Logger.getLogger(InitialConnector.class.getName());

    @Setter
    private boolean isRetry;

    public ChannelFuture connect() {
        Bootstrap b = new Bootstrap();
        b.group(ZNetworkPlugin.getInstance().getWorkerGroup());
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                PipelineInitializer.initChannel(ch, false);
                ch.pipeline().addLast(new MessageHandler());
            }
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);

        log.info("Connecting...");
        if (System.getProperty("destinationPort") == null) {
            return b.connect("178.63.117.132", 50000);
        } else {
            return b.connect("178.63.117.132", Integer.valueOf(System.getProperty("destinationPort")));
        }
    }

    public void startConnection() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (isRetry) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ChannelFuture future = connect();
                try {
                    //noinspection ThrowableResultOfMethodCallIgnored
                    while (!future.await(5, TimeUnit.SECONDS) || future.cause() != null) {
                        log.log(Level.SEVERE, "Error connecting to network server", future.cause());
                        future.cancel(true);
                        future.channel().close().sync();
                        Thread.sleep(2000);
                        // TODO cancel etc
                        future = connect();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}

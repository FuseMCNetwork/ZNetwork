package com.xxmicloxx.znetworkserver;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.xxmicloxx.znetworklib.PipelineInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ml
 * Date: 17.12.13
 * Time: 19:55
 */
public class ZNetworkServer
{
    @Getter
    private static ZNetworkServer instance;

    @Getter
    private Map<String, MessageHandler> channelMap = new ConcurrentHashMap<String, MessageHandler>();

    @Getter
    private Multimap<String, MessageHandler> eventMap = Multimaps.synchronizedMultimap(HashMultimap.<String, MessageHandler>create());

    public void onEnable() {
        System.out.println("Starting up...");

        instance = this;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .childHandler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel socketChannel) throws Exception {
                 PipelineInitializer.initChannel(socketChannel, true);
                 socketChannel.pipeline().addLast(new MessageHandler());
             }
         })
         .option(ChannelOption.SO_BACKLOG, 128)
         .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future;
        if (System.getProperty("destinationPort") == null) {
            future = b.bind(50000);
        } else {
            future = b.bind(Integer.valueOf(System.getProperty("destinationPort")));
        }

        try {
            future.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String... args) {
        new ZNetworkServer().onEnable();
    }
}

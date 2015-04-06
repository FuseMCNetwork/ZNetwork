package com.xxmicloxx.znetworklib;

import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.codec.Packet;
import com.xxmicloxx.znetworklib.codec.PacketWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by ml on 14.08.14.
 */
public class MessageEncoder extends MessageToMessageEncoder<NetworkPacket> {
    private boolean isNetwork = false;

    public MessageEncoder(boolean isNetwork) {
        this.isNetwork = isNetwork;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkPacket msg, List<Object> out) throws Exception {
        int id = PacketRegistry.getPacketId(msg.getClass());

        ByteBuf buf = ctx.alloc().buffer();
        PacketWriter writer = new PacketWriter(buf);

        writer.writeInt(id);

        if (isNetwork) {
            msg.writeNetwork(writer);
        } else {
            msg.write(writer);
        }

        out.add(buf);
    }
}

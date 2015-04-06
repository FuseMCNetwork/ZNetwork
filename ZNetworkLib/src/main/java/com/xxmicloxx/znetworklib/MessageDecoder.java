package com.xxmicloxx.znetworklib;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.codec.Packet;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ml on 14.08.14.
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private boolean isNetwork = false;

    public MessageDecoder(boolean isNetwork) {
        this.isNetwork = isNetwork;
    }

    private Logger log = Logger.getLogger(MessageDecoder.class.getName());

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        int id = byteBuf.readInt();
        PacketReader reader = new PacketReader(byteBuf);

        NetworkPacket packet = (NetworkPacket) PacketRegistry.getPacketInstance(id);

        CodecResult result;
        if (isNetwork) {
            result = packet.readNetwork(reader);
        } else {
            result = packet.read(reader);
        }

        if (result == CodecResult.SKIP) {
            log.severe("ERROR while decoding packet with id " + id);
            return;
        }

        objects.add(packet);
    }
}

package com.xxmicloxx.znetworklib;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by ml on 14.08.14.
 */
public class PacketSlicer extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        while (byteBuf.readableBytes() > 0) {
            byteBuf.markReaderIndex();
            if (byteBuf.readableBytes() < 4) {
                // no int
                return;
            }

            int readableBytes = byteBuf.readInt();
            if (byteBuf.readableBytes() < readableBytes) {
                byteBuf.resetReaderIndex();
                return;
            }

            objects.add(byteBuf.copy(byteBuf.readerIndex(), readableBytes));
            byteBuf.skipBytes(readableBytes);
        }
    }
}

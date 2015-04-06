package com.xxmicloxx.znetworklib.packet.core;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworklib.codec.*;
import com.xxmicloxx.znetworklib.exception.PacketCodecException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by ml on 04.07.14.
 */
public class EmitEventRequest implements NetworkPacket {
    private String event;
    private String sender;

    /**
     * Client-ONLY
     */
    private NetworkEvent data;

    /**
     * NetworkServer-ONLY
     */
    private int packetId;

    /**
     * NetworkServer-ONLY
     */
    private byte[] packetData;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public NetworkEvent getData() {
        return data;
    }

    public void setData(NetworkEvent data) {
        this.data = data;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public byte[] getPacketData() {
        return packetData;
    }

    public void setPacketData(byte[] packetData) {
        this.packetData = packetData;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(event);
        writer.writeString(sender);
        if (data != null) {
            writer.writeInt(PacketRegistry.getPacketId(data));
            ByteBuf buffer = Unpooled.buffer();
            PacketWriter tempWriter = new PacketWriter(buffer);
            if (data.write(tempWriter) != CodecResult.OK) {
                buffer.release();
                return CodecResult.SKIP;
            }
            writer.writeInt(buffer.readableBytes());
            writer.writeBytes(buffer);
            buffer.release();
        } else {
            writer.writeInt(Integer.MIN_VALUE);
            writer.writeInt(0);
        }
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        throw new PacketCodecException("Don't use this method.", null);
    }

    @Override
    public CodecResult readNetwork(PacketReader reader) {
        event = reader.readString();
        sender = reader.readString();
        packetId = reader.readInt();
        packetData = new byte[reader.readInt()];
        reader.readBytes(packetData);
        return CodecResult.OK;
    }

    @Override
    public CodecResult writeNetwork(PacketWriter writer) {
        throw new PacketCodecException("Don't use this method.", null);
    }
}

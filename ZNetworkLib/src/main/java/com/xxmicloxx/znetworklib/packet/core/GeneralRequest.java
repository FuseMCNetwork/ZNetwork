package com.xxmicloxx.znetworklib.packet.core;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworklib.codec.*;
import com.xxmicloxx.znetworklib.exception.PacketCodecException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

/**
 * Created by ml on 09.07.14.
 */
public class GeneralRequest implements NetworkPacket {
    private UUID handle;
    private String target;
    private String sender;

    /**
     * Client-ONLY
     */
    private Request request;

    /**
     * NetworkServer-ONLY
     */
    private int requestId;

    /**
     * NetworkServer-ONLY
     */
    private byte[] requestData;

    public UUID getHandle() {
        return handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public byte[] getRequestData() {
        return requestData;
    }

    public void setRequestData(byte[] requestData) {
        this.requestData = requestData;
    }

    @Override
    public CodecResult readNetwork(PacketReader reader) {
        handle = reader.readUUID();
        target = reader.readString();
        sender = reader.readString();
        requestId = reader.readInt();
        requestData = new byte[reader.readInt()];
        reader.readBytes(requestData);
        return CodecResult.OK;
    }

    @Override
    public CodecResult writeNetwork(PacketWriter writer) {
        writer.writeUUID(handle);
        writer.writeString(target);
        writer.writeString(sender);
        writer.writeInt(requestId);
        writer.writeInt(requestData.length);
        writer.writeBytes(requestData);
        return CodecResult.OK;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeUUID(handle);
        writer.writeString(target);
        writer.writeString(sender);
        if (request != null) {
            writer.writeInt(PacketRegistry.getPacketId(request));
            ByteBuf buffer = Unpooled.buffer();
            PacketWriter tempWriter = new PacketWriter(buffer);
            if (request.write(tempWriter) != CodecResult.OK) {
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
        handle = reader.readUUID();
        target = reader.readString();
        sender = reader.readString();
        int packetId = reader.readInt();
        if (packetId != Integer.MIN_VALUE) {
            Class<? extends Packet> request = PacketRegistry.getPacket(packetId);
            if (request == null) {
                System.err.println("Received a request from " + sender + " without registered packet.");
                return CodecResult.SKIP;
            }
            try {
                this.request = (Request) request.newInstance();
            } catch (InstantiationException e) {
                throw new PacketCodecException("Error while reading Broadcast: ", e);
            } catch (IllegalAccessException e) {
                throw new PacketCodecException("Error while reading Broadcast: ", e);
            }
            reader.readInt(); // nobody needs that length
            return this.request.read(reader);
        } else {
            reader.readInt();
            return CodecResult.OK;
        }
    }
}

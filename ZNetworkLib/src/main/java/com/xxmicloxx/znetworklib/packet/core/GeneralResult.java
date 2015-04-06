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
public class GeneralResult implements NetworkPacket {
    private UUID handle;
    private String target;
    private String sender;

    /**
     * Client-ONLY
     */
    private Result result;

    /**
     * NetworkServer-ONLY
     */
    private int resultId;

    /**
     * NetworkServer-ONLY
     */
    private byte[] resultData;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public byte[] getResultData() {
        return resultData;
    }

    public void setResultData(byte[] resultData) {
        this.resultData = resultData;
    }

    @Override
    public CodecResult readNetwork(PacketReader reader) {
        handle = reader.readUUID();
        target = reader.readString();
        sender = reader.readString();
        resultId = reader.readInt();
        resultData = new byte[reader.readInt()];
        reader.readBytes(resultData);
        return CodecResult.OK;
    }

    @Override
    public CodecResult writeNetwork(PacketWriter writer) {
        writer.writeUUID(handle);
        writer.writeString(target);
        writer.writeString(sender);
        writer.writeInt(resultId);
        writer.writeInt(resultData.length);
        writer.writeBytes(resultData);
        return CodecResult.OK;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeUUID(handle);
        writer.writeString(target);
        writer.writeString(sender);
        if (result != null) {
            writer.writeInt(PacketRegistry.getPacketId(result));
            ByteBuf buffer = Unpooled.buffer();
            PacketWriter tempWriter = new PacketWriter(buffer);
            if (result.write(tempWriter) != CodecResult.OK) {
                buffer.release();
                return CodecResult.SKIP;
            }
            writer.writeInt(buffer.readableBytes());
            writer.writeBytes(buffer);
            buffer.release();
        } else {
            // take id min
            writer.writeInt(Integer.MIN_VALUE);
            // length
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
                System.err.println("Received a result from " + sender + " without registered packet.");
                return CodecResult.SKIP;
            }
            try {
                this.result = (Result) request.newInstance();
            } catch (InstantiationException e) {
                throw new PacketCodecException("Error while reading Broadcast: ", e);
            } catch (IllegalAccessException e) {
                throw new PacketCodecException("Error while reading Broadcast: ", e);
            }
            reader.readInt(); // nobody needs that length
            return this.result.read(reader);
        } else {
            reader.readInt();
            return CodecResult.OK;
        }
    }
}

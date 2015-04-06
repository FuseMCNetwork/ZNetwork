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
public class GeneralRequestTargetNotFound implements NetworkPacket {
    private UUID handle;
    private String target;
    private String sender;

    public GeneralRequestTargetNotFound(GeneralRequest request) {
        handle = request.getHandle();
        target = request.getTarget();
        sender = request.getSender();
    }

    public GeneralRequestTargetNotFound() {
    }

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

    @Override
    public CodecResult readNetwork(PacketReader reader) {
        throw new PacketCodecException("Don't use this method.", null);
    }

    @Override
    public CodecResult writeNetwork(PacketWriter writer) {
        writer.writeUUID(handle);
        writer.writeString(target);
        writer.writeString(sender);
        return CodecResult.OK;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        throw new PacketCodecException("Don't use this method.", null);
    }

    @Override
    public CodecResult read(PacketReader reader) {
        handle = reader.readUUID();
        target = reader.readString();
        sender = reader.readString();
        return CodecResult.OK;
    }
}

package com.xxmicloxx.znetworklib.packet.core;

import com.xxmicloxx.znetworklib.codec.*;

/**
 * Created by ml on 09.07.14.
 */
public class HaveNameRequest implements NetworkPacket {
    private String desiredName;

    public String getDesiredName() {
        return desiredName;
    }

    public void setDesiredName(String desiredName) {
        this.desiredName = desiredName;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(desiredName);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        desiredName = reader.readString();
        return CodecResult.OK;
    }

    @Override
    public CodecResult readNetwork(PacketReader reader) {
        return read(reader);
    }

    @Override
    public CodecResult writeNetwork(PacketWriter writer) {
        return write(writer);
    }
}

package com.xxmicloxx.znetworklib.packet.core;

import com.xxmicloxx.znetworklib.codec.*;

/**
 * Created by ml on 09.07.14.
 */
public class HaveNameResult implements NetworkPacket {
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeBoolean(successful);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        successful = reader.readBoolean();
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

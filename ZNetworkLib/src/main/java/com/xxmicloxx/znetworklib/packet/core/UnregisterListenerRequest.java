package com.xxmicloxx.znetworklib.packet.core;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkPacket;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

/**
 * Created by ml on 09.07.14.
 */
public class UnregisterListenerRequest implements NetworkPacket {
    private String event;
    private String sender;

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

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(event);
        writer.writeString(sender);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        event = reader.readString();
        sender = reader.readString();
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

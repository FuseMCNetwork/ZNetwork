package com.xxmicloxx.znetworklib.codec;

/**
 * Created by ml on 04.07.14.
 */
public interface NetworkPacket extends Packet {
    public CodecResult readNetwork(PacketReader reader);

    public CodecResult writeNetwork(PacketWriter writer);
}

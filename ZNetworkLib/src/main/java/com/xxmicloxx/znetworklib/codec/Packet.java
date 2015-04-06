package com.xxmicloxx.znetworklib.codec;

/**
 * Created by ml on 04.07.14.
 */
public interface Packet {
    public CodecResult write(PacketWriter writer);

    public CodecResult read(PacketReader reader);
}

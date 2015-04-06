package me.johnking.zmaster;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

/**
 * Created by Marco on 10.10.2014.
 */
public class StartServerPacket implements NetworkEvent {

    private String typeID;

    public StartServerPacket() {

    }

    public StartServerPacket(String typeID) {
        this.typeID = typeID;
    }

    @Override
    public CodecResult write(PacketWriter packetWriter) {
        packetWriter.writeString(typeID);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader packetReader) {
        this.typeID = packetReader.readString();
        return CodecResult.OK;
    }

    public String getTypeID() {
        return typeID;
    }
}

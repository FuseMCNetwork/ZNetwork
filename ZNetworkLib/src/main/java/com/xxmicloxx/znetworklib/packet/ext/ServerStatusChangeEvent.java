package com.xxmicloxx.znetworklib.packet.ext;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

/**
 * Created by Marco on 15.08.2014.
 */
public class ServerStatusChangeEvent implements NetworkEvent {

    private String serverId;
    private String gamemode;
    private String address;
    private int port;

    public ServerStatusChangeEvent(){

    }

    public ServerStatusChangeEvent(String serverId, String gamemode, String address, int port){
        this.serverId = serverId;
        this.gamemode = gamemode;
        this.address = address;
        this.port = port;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(serverId);
        writer.writeString(gamemode);
        writer.writeString(address);
        writer.writeInt(port);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        serverId = reader.readString();
        gamemode = reader.readString();
        address = reader.readString();
        port = reader.readInt();
        return CodecResult.OK;
    }

    public String getServerId(){
        return serverId;
    }

    public String getGamemode(){
        return gamemode;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public static void register() {
        PacketRegistry.registerPacket(ServerStatusChangeEvent.class, 1000);
    }
}

package com.xxmicloxx.znetworklib.packet.ext;

import com.xxmicloxx.znetworklib.MinecraftServer;
import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;
import com.xxmicloxx.znetworklib.codec.Result;

import java.util.ArrayList;

/**
 * Created by ml on 04.07.14.
 */
public class GetChildServersResult implements Result {
    private ArrayList<MinecraftServer> servers = new ArrayList<MinecraftServer>();

    public ArrayList<MinecraftServer> getServers() {
        return servers;
    }

    @Override
    public com.xxmicloxx.znetworklib.codec.CodecResult write(PacketWriter writer) {
        writer.writeInt(servers.size());
        for (MinecraftServer server : servers) {
            server.write(writer);
        }
        return CodecResult.OK;
    }

    @Override
    public com.xxmicloxx.znetworklib.codec.CodecResult read(PacketReader reader) {
        int max = reader.readInt();
        for (int i = 0; i < max; i++) {
            servers.add(MinecraftServer.read(reader));
        }
        return CodecResult.OK;
    }
}

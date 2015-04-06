package me.johnking.zmaster;

import com.xxmicloxx.znetworklib.PacketRegistry;
import me.johnking.zmaster.server.DynamicServer;
import me.johnking.zmaster.server.DynamicServerType;
import me.johnking.zmaster.server.Server;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworkplugin.EventListener;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;

import java.util.logging.Level;

/**
 * Created by Marco on 15.08.2014.
 */
public final class DynamicServerHandler {

    private DynamicServerHandler() {
    }

    public static void init(){
        PacketRegistry.registerPacket(StartServerPacket.class, 91872498);
        ZNetworkPlugin.getInstance().registerEvent("serversigns_startserver", new DynamicServerListener());
    }

    public static void checkServerTypes() {
        for (DynamicServerType type : DynamicServerType.getTypes()) {
            if (type.shouldStartServers()) {
                int delta = type.getMinServers() - type.getLobbyDisplayedAndQueuedServerCount();
                for (int i = 0; i < delta; i++) {
                    ZMaster.getInstance().getQueue().queue(type.getFreeServer());
                }
            }
        }
    }

    private static final class DynamicServerListener implements EventListener{

        @Override
        public void onEventReceived(String event, String sender, NetworkEvent data) {
            if(event.equals("serversigns_startserver") && data instanceof StartServerPacket) {
                StartServerPacket packet = (StartServerPacket) data;
                Server server = Server.getServer(packet.getTypeID());
                if(server == null || !(server instanceof DynamicServer)){
                    return;
                }
                ZMaster.getLogger().log(Level.INFO, packet.getTypeID() + " is now vip or in game!");
                DynamicServer dynamicServer = (DynamicServer) server;
                dynamicServer.setInGame();
                checkServerTypes();
            }
        }
    }
}

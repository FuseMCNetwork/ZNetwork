package com.xxmicloxx.znetworklib;

import com.xxmicloxx.znetworklib.codec.Packet;
import com.xxmicloxx.znetworklib.packet.core.*;
import com.xxmicloxx.znetworklib.packet.ext.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ml on 04.07.14.
 */
public final class PacketRegistry {

    private static final Map<Integer, Class<? extends Packet>> idToPacket = new ConcurrentHashMap<Integer, Class<? extends Packet>>();
    private static final Map<Class<? extends Packet>, Integer> packetToId = new ConcurrentHashMap<Class<? extends Packet>, Integer>();

    public static void registerPacket(Class<? extends Packet> clazz, int id){
        idToPacket.put(id, clazz);
        packetToId.put(clazz, id);
    }

    public static int getPacketId(Class<? extends Packet> packet) {
        return packetToId.get(packet);
    }

    public static int getPacketId(Packet packet) {
        return getPacketId(packet.getClass());
    }

    public static Class<? extends Packet> getPacket(int id) {
        return idToPacket.get(id);
    }

    public static Packet getPacketInstance(int id) throws Exception {
        Class<? extends Packet> clazz = getPacket(id);
        if(clazz == null){
            return null;
        }

        return clazz.newInstance();
    }

    static {
        //core packets
        registerPacket(EmitEventRequest.class, 0);
        registerPacket(EventEmittedRequest.class, 1);
        registerPacket(GeneralRequest.class, 2);
        registerPacket(GeneralResult.class, 3);
        registerPacket(HaveNameRequest.class, 4);
        registerPacket(HaveNameResult.class, 5);
        registerPacket(RegisterListenerRequest.class, 6);

        //ext packets
        registerPacket(GetChildServersRequest.class, 7);
        registerPacket(GetChildServersResult.class, 8);
        registerPacket(ServerStatusChangeEvent.class, 9);
    }
}

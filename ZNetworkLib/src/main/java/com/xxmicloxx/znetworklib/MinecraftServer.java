package com.xxmicloxx.znetworklib;

import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

import java.io.Serializable;

/**
* Created with IntelliJ IDEA.
* User: ml
* Date: 11.01.14
* Time: 22:56
*/
public class MinecraftServer implements Serializable {
    private static final long serialVersionUID = -5878986704766680722L;

    private String name;
    private String address;
    private int port;
    private String type;
    private boolean multiSession;
    private int heap;
    private String master;

    private boolean running;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMultiSession() {
        return multiSession;
    }

    public void setMultiSession(boolean multiSession) {
        this.multiSession = multiSession;
    }

    public int getHeap() {
        return heap;
    }

    public void setHeap(int heap) {
        this.heap = heap;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinecraftServer server = (MinecraftServer) o;

        return name.equals(server.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void write(PacketWriter writer) {
        writer.writeString(name);
        writer.writeString(address);
        writer.writeShort(port);
        writer.writeString(type);
        writer.writeBoolean(multiSession);
        writer.writeInt(heap);
        writer.writeString(master);
    }

    public static MinecraftServer read(PacketReader reader) {
        MinecraftServer server = new MinecraftServer();
        server.name = reader.readString();
        server.address = reader.readString();
        server.port = reader.readUnsignedShort();
        server.type = reader.readString();
        server.multiSession = reader.readBoolean();
        server.heap = reader.readInt();
        server.master = reader.readString();
        return server;
    }
}

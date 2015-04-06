package com.xxmicloxx.znetworklib.packet.core;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworklib.codec.*;
import com.xxmicloxx.znetworklib.exception.PacketCodecException;

/**
 * Created by ml on 09.07.14.
 */
public class EventEmittedRequest implements NetworkPacket {
    private String event;
    private String sender;

    /**
     * Client-ONLY
     */
    private NetworkEvent data;

    /**
     * NetworkServer-ONLY
     */
    private int packetId;

    /**
     * NetworkServer-ONLY
     */
    private byte[] packetData;

    public EventEmittedRequest(EmitEventRequest request) {
        event = request.getEvent();
        sender = request.getSender();
        data = request.getData();
        packetId = request.getPacketId();
        packetData = request.getPacketData();
    }

    public EventEmittedRequest() {
    }

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

    public NetworkEvent getData() {
        return data;
    }

    public void setData(NetworkEvent data) {
        this.data = data;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public byte[] getPacketData() {
        return packetData;
    }

    public void setPacketData(byte[] packetData) {
        this.packetData = packetData;
    }

    @Override
    public CodecResult readNetwork(PacketReader reader) {
        throw new PacketCodecException("Don't use this method.", null);
    }

    @Override
    public CodecResult writeNetwork(PacketWriter writer) {
        writer.writeString(event);
        writer.writeString(sender);
        writer.writeInt(packetId);
        writer.writeInt(packetData.length);
        writer.writeBytes(packetData);
        return CodecResult.OK;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        throw new PacketCodecException("Don't use this method.", null);
    }

    @Override
    public CodecResult read(PacketReader reader) {
        event = reader.readString();
        sender = reader.readString();
        int packetId = reader.readInt();
        if (packetId != Integer.MIN_VALUE) {
            Class<? extends Packet> event = PacketRegistry.getPacket(packetId);
            if (event == null) {
                System.err.println("Subscribed to event " + this.event + " but could not read data!");
                return CodecResult.SKIP;
            }
            try {
                data = (NetworkEvent) event.newInstance();
            } catch (InstantiationException e) {
                throw new PacketCodecException("Error while reading Broadcast: ", e);
            } catch (IllegalAccessException e) {
                throw new PacketCodecException("Error while reading Broadcast: ", e);
            }
            reader.readInt(); // go away length, don't need you
            return data.read(reader);
        } else {
            reader.readInt();
            return CodecResult.OK;
        }
    }
}

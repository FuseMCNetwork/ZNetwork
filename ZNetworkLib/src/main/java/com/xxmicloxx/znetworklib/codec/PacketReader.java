package com.xxmicloxx.znetworklib.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.UUID;

/**
 * Created by ml on 04.07.14.
 */
public final class PacketReader {
    private final ByteBuf byteBuf;

    public PacketReader(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
        this.byteBuf.markReaderIndex();
    }

    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    public byte readByte() {
        return byteBuf.readByte();
    }

    public short readUnsignedByte() {
        return byteBuf.readUnsignedByte();
    }

    public short readShort() {
        return byteBuf.readShort();
    }

    public int readUnsignedShort() {
        return byteBuf.readUnsignedShort();
    }

    public int readMedium() {
        return byteBuf.readMedium();
    }

    public int readUnsignedMedium() {
        return byteBuf.readUnsignedMedium();
    }

    public int readInt() {
        return byteBuf.readInt();
    }

    public long readUnsignedInt() {
        return byteBuf.readUnsignedInt();
    }

    public long readLong() {
        return byteBuf.readLong();
    }

    public char readChar() {
        return byteBuf.readChar();
    }

    public float readFloat() {
        return byteBuf.readFloat();
    }

    public double readDouble() {
        return byteBuf.readDouble();
    }

    public void readBytes(int i) {
        byteBuf.readBytes(i);
    }

    public void readSlice(int i) {
        byteBuf.readSlice(i);
    }

    public void readBytes(ByteBuf byteBuf) {
        this.byteBuf.readBytes(byteBuf);
    }

    public void readBytes(ByteBuf byteBuf, int i) {
        this.byteBuf.readBytes(byteBuf, i);
    }

    public void readBytes(ByteBuf byteBuf, int i, int i2) {
        this.byteBuf.readBytes(byteBuf, i, i2);
    }

    public void readBytes(byte[] bytes) {
        byteBuf.readBytes(bytes);
    }

    public void readBytes(byte[] bytes, int i, int i2) {
        byteBuf.readBytes(bytes, i, i2);
    }

    public void readBytes(ByteBuffer byteBuffer) {
        byteBuf.readBytes(byteBuffer);
    }

    public void readBytes(OutputStream outputStream, int i) throws IOException {
        byteBuf.readBytes(outputStream, i);
    }

    public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
        return byteBuf.readBytes(gatheringByteChannel, i);
    }

    public String readString() {
        int length = readUnsignedShort();
        byte[] stringData = new byte[length];
        readBytes(stringData);
        try {
            return new String(stringData, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return null; //unreachable statement
    }

    public boolean hasBytes(){
        return byteBuf.readerIndex() < byteBuf.capacity() - 1;
    }

    public UUID readUUID() {
        long msb = readLong();
        long lsb = readLong();
        return new UUID(msb, lsb);
    }
}

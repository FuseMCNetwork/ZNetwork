package com.xxmicloxx.znetworklib.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.util.UUID;

/**
 * Created by ml on 04.07.14.
 */
public final class PacketWriter {
    private final ByteBuf byteBuf;

    public PacketWriter(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
        this.byteBuf.markWriterIndex();
    }

    public void writeBoolean(boolean b) {
        byteBuf.writeBoolean(b);
    }

    public void writeByte(int i) {
        byteBuf.writeByte(i);
    }

    public void writeShort(int i) {
        byteBuf.writeShort(i);
    }

    public void writeMedium(int i) {
        byteBuf.writeMedium(i);
    }

    public void writeInt(int i) {
        byteBuf.writeInt(i);
    }

    public void writeLong(long l) {
        byteBuf.writeLong(l);
    }

    public void writeChar(int i) {
        byteBuf.writeChar(i);
    }

    public void writeFloat(float v) {
        byteBuf.writeFloat(v);
    }

    public void writeDouble(double v) {
        byteBuf.writeDouble(v);
    }

    public void writeBytes(ByteBuf byteBuf) {
        this.byteBuf.writeBytes(byteBuf);
    }

    public void writeBytes(ByteBuf byteBuf, int i) {
        this.byteBuf.writeBytes(byteBuf, i);
    }

    public void writeBytes(ByteBuf byteBuf, int i, int i2) {
        this.byteBuf.writeBytes(byteBuf, i, i2);
    }

    public void writeBytes(byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }

    public void writeBytes(byte[] bytes, int i, int i2) {
        byteBuf.writeBytes(bytes, i, i2);
    }

    public void writeBytes(ByteBuffer byteBuffer) {
        byteBuf.writeBytes(byteBuffer);
    }

    public int writeBytes(InputStream inputStream, int i) throws IOException {
        return byteBuf.writeBytes(inputStream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
        return byteBuf.writeBytes(scatteringByteChannel, i);
    }

    public void writeString(String s) {
        try {
            byte[] string = s.getBytes("UTF-8");
            writeShort(string.length);
            writeBytes(string);
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    public void writeZero(int i) {
        byteBuf.writeZero(i);
    }

    public void resetWriterIndex() {
        byteBuf.resetWriterIndex();
    }

    public void writeUUID(UUID handle) {
        long msb = handle.getMostSignificantBits();
        long lsb = handle.getLeastSignificantBits();
        writeLong(msb);
        writeLong(lsb);
    }
}

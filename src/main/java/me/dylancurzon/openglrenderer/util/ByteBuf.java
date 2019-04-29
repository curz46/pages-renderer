package me.dylancurzon.openglrenderer.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A simple wrapper utility class which allows reading strings from a ByteBuffer.
 */
public class ByteBuf {

    private final ByteBuffer buffer;

    public ByteBuf(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    public int remaining() {
        return buffer.remaining();
    }

    public void flip() {
        buffer.flip();
    }

    public void writeString(String value, int length) {
        byte[] buf = new byte[length];
        byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode String value as UTF-8.", e);
        }
        System.arraycopy(bytes, 0, buf, 0, bytes.length);
        buffer.put(buf, 0, buf.length);
    }

    public void writeInt(int value) {
        buffer.putInt(value);
    }

    public void writeDouble(double value) {
        buffer.putDouble(value);
    }

    public void writeLong(long value) {
        buffer.putLong(value);
    }

    public void writeShort(short value) {
        buffer.putShort(value);
    }

    public void writeByte(byte value) {
        buffer.put(value);
    }

    public String readString(int length) {
        byte[] buf = new byte[length];
        buffer.get(buf, 0, length);
        return new String(buf, StandardCharsets.UTF_8);
    }

    public int readInt() {
        return buffer.getInt();
    }

    public double readDouble() {
        return buffer.getDouble();
    }

    public long readLong() {
        return buffer.getLong();
    }

    public short readShort() {
        return buffer.getShort();
    }

    public byte readByte() {
        return buffer.get();
    }

}

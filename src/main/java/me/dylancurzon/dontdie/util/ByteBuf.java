package me.dylancurzon.dontdie.util;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A simple wrapper utility class which allows reading strings from a ByteBuffer.
 */
@Immutable
public class ByteBuf {

    @NotNull
    private final ByteBuffer buffer;

    public ByteBuf(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean hasRemaining() {
        return this.buffer.hasRemaining();
    }

    public int remaining() {
        return this.buffer.remaining();
    }

    public void flip() {
        this.buffer.flip();
    }

    public void writeString(@NotNull final String value, @NotNull final int length) {
        final byte[] buf = new byte[length];
        final byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode String value as UTF-8.", e);
        }
        System.arraycopy(bytes, 0, buf, 0, bytes.length);
        this.buffer.put(buf, 0, buf.length);
    }

    public void writeInt(@NotNull final int value) {
        this.buffer.putInt(value);
    }

    public void writeDouble(@NotNull final double value) {
        this.buffer.putDouble(value);
    }

    public void writeLong(@NotNull final long value) {
        this.buffer.putLong(value);
    }

    public void writeShort(@NotNull final short value) {
        this.buffer.putShort(value);
    }

    public void writeByte(@NotNull final byte value) {
        this.buffer.put(value);
    }

    public String readString(@NotNull final int length) {
        final byte[] buf = new byte[length];
        this.buffer.get(buf, 0, length);
        return new String(buf, StandardCharsets.UTF_8);
    }

    public int readInt() {
        return this.buffer.getInt();
    }

    public double readDouble() {
        return this.buffer.getDouble();
    }

    public long readLong() {
        return this.buffer.getLong();
    }

    public short readShort() {
        return this.buffer.getShort();
    }

    public byte readByte() {
        return this.buffer.get();
    }

}

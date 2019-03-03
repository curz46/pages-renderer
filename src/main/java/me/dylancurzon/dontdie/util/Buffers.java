package me.dylancurzon.dontdie.util;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class Buffers {

    public static ByteBuffer asHeapBuffer(final byte[] pixels) {
        final ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length);
        buffer.put(pixels);
        buffer.flip();
        return buffer;
    }

    public static byte[] asByteArray(final ByteBuffer buffer) {
        buffer.position(0);
        final byte[] pixels = new byte[buffer.remaining()];
        buffer.get(pixels);
        return pixels;
    }

}

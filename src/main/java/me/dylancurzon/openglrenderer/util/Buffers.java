package me.dylancurzon.openglrenderer.util;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class Buffers {

    public static ByteBuffer asHeapBuffer(byte[] pixels) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length);
        buffer.put(pixels);
        buffer.flip();
        return buffer;
    }

    public static byte[] asByteArray(ByteBuffer buffer) {
        buffer.position(0);
        byte[] pixels = new byte[buffer.remaining()];
        buffer.get(pixels);
        return pixels;
    }

}

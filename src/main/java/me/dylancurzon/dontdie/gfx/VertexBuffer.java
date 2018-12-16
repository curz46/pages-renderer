package me.dylancurzon.dontdie.gfx;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {

    public static VertexBuffer make() {
        return new VertexBuffer(glGenBuffers());
    }

    public static void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private final int id;

    private VertexBuffer(final int id) {
        this.id = id;
    }

    public void upload(final short[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(final double[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(final int[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(final float[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(final long[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
    }

    public void destroy() {
        glDeleteBuffers(this.id);
    }

}

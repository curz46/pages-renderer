package me.dylancurzon.dontdie.gfx.opengl;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {

    public static VertexBuffer make() {
        return new VertexBuffer(glGenBuffers());
    }

    public static void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private final int id;

    private VertexBuffer(int id) {
        this.id = id;
    }

    public void upload(short[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(double[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(int[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(float[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void upload(long[] data) {
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id);
    }

    public void destroy() {
        glDeleteBuffers(id);
    }

}

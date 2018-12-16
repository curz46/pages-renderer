package me.dylancurzon.dontdie.gfx;

public interface Renderer {

    /**
     * Prepare this Renderer.
     */
    void prepare();

    /**
     * Cleanup this Renderer.
     */
    void cleanup();

    /**
     * Render to the Display.
     */
    void render();

}

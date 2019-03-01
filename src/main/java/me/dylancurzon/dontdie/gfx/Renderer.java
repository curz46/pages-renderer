package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.Renderable;

public interface Renderer extends Renderable {

    /**
     * Prepare this Renderer.
     */
    void prepare();

    /**
     * Cleanup this Renderer.
     */
    void cleanup();

}

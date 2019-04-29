package me.dylancurzon.openglrenderer.gfx;

import me.dylancurzon.openglrenderer.Renderable;

public abstract class Renderer implements Renderable {

    /**
     * A dirty renderer is one which needs to update its buffers to reflect changed state.
     */
    private boolean dirty;

    /**
     * Prepare this Renderer.
     */
    public abstract void prepare();

    /**
     * Cleanup this Renderer.
     */
    public abstract void cleanup();

    /**
     * Update this Renderer.
     */
    public abstract void update();

    /**
     * Set this {@link Renderer} as dirty.
     * @return The old value
     * @see this#isDirty()
     */
    public boolean setDirty(boolean newValue) {
        boolean oldValue = dirty;
        dirty = newValue;
        return oldValue;
    }

    /**
     * @return {@code true} only if this {@link Renderer} is dirty.
     * @see this#dirty
     */
    public boolean isDirty() {
        return dirty;
    }

}

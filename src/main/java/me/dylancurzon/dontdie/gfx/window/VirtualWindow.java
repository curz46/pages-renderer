package me.dylancurzon.dontdie.gfx.window;

import me.dylancurzon.pages.event.bus.SimpleEventBus;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;

/**
 * A virtual window is a wrapper for an existing {@link Window} with a different size. It is intended for use with
 * OpenGL rendering such that rendering can act on the virtual resolution and then be scaled up to the window
 * resolution.
 */
public class VirtualWindow extends SimpleEventBus implements Window {

    private final Window window;
    private final Vector2i virtualDimensions;

    public VirtualWindow(Window window, Vector2i virtualDimensions) {
        this.window = window;
        this.virtualDimensions = virtualDimensions;

        // Forward all events
        window.subscribe(Object.class, this::post);
    }

    @Override
    public long getId() {
        return window.getId();
    }

    @Override
    public String getTitle() {
        return window.getTitle();
    }

    @Override
    public Vector2i getDimensions() {
        return virtualDimensions;
    }

    @Override
    public Vector2d getMousePosition() {
        Vector2d realPosition = window.getMousePosition();
        if (realPosition == null) return null;

        // Linearly interpolate to the virtual dimensions
        return realPosition
            .div(window.getDimensions().toDouble())
            .mul(virtualDimensions.toDouble());
    }

    @Override
    public boolean isFocused() {
        return window.isFocused();
    }

    @Override
    public boolean isKeyPressed(int code) {
        return window.isKeyPressed(code);
    }

    @Override
    public boolean isMousePressed(MouseButton button) {
        return window.isMousePressed(button);
    }

}

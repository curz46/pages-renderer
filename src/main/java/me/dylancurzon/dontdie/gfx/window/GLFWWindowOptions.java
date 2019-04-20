package me.dylancurzon.dontdie.gfx.window;

import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.Objects;

public class GLFWWindowOptions {

    private final Vector2i dimensions;
    private final String title;
    private final boolean vsync;
    private final boolean visible;
    private final boolean resizable;
    @Nullable
    private final GLFWErrorCallback errorCallback;

    public static SetDimensions builder() {
        return new Builder();
    }

    private GLFWWindowOptions(Vector2i dimensions,
                             String title,
                             boolean vsync,
                             boolean visible,
                             boolean resizable,
                             @Nullable GLFWErrorCallback errorCallback) {
        this.dimensions = Objects.requireNonNull(dimensions);
        this.title = Objects.requireNonNull(title);
        this.vsync = vsync;
        this.visible = visible;
        this.resizable = resizable;
        this.errorCallback = errorCallback;
    }

    public Vector2i getDimensions() {
        return dimensions;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVsync() {
        return vsync;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isResizable() {
        return resizable;
    }

    @Nullable
    public GLFWErrorCallback getErrorCallback() {
        return errorCallback;
    }

    public static class Builder implements SetDimensions, SetTitle {

        private Vector2i dimensions;
        private String title;
        private boolean vsync = false;
        private boolean visible = false;
        private boolean resizable = false;
        @Nullable
        private GLFWErrorCallback errorCallback = null;

        @Override
        public SetTitle setDimensions(Vector2i dimensions) {
            this.dimensions = Objects.requireNonNull(dimensions);
            return this;
        }

        @Override
        public Builder setTitle(String title) {
            this.title = Objects.requireNonNull(title);
            return this;
        }

        public Builder setVsync(boolean vsync) {
            this.vsync = vsync;
            return this;
        }

        public Builder setVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder setResizable(boolean resizable) {
            this.resizable = resizable;
            return this;
        }

        public Builder setErrorCallback(@Nullable GLFWErrorCallback errorCallback) {
            this.errorCallback = errorCallback;
            return this;
        }

        public GLFWWindowOptions build() {
            if (dimensions == null) {
                throw new IllegalArgumentException("Window dimensions must be set for GLFWWindowOptions!");
            }
            if (title == null) {
                throw new IllegalArgumentException("Window title must be set for GLFWWindowOptions!");
            }

            return new GLFWWindowOptions(dimensions, title, vsync, visible,resizable, errorCallback);
        }

    }

    public interface SetDimensions {
        SetTitle setDimensions(Vector2i dimensions);
    }

    public interface SetTitle {
        Builder setTitle(String title);
    }

}

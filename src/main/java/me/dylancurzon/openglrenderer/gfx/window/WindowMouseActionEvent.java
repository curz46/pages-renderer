package me.dylancurzon.openglrenderer.gfx.window;

import me.dylancurzon.pages.util.MouseButton;

public abstract class WindowMouseActionEvent {

    public static class Press extends WindowMouseActionEvent {
        public Press(MouseAction action, MouseButton button) {
            super(action, button);
        }
    }

    public static class Release extends WindowMouseActionEvent {
        public Release(MouseAction action, MouseButton button) {
            super(action, button);
        }
    }

    private final MouseAction action;
    private final MouseButton button;

    private WindowMouseActionEvent(MouseAction action, MouseButton button) {
        this.action = action;
        this.button = button;
    }

    public MouseAction getAction() {
        return action;
    }

    public MouseButton getButton() {
        return button;
    }

}

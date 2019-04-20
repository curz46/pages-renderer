package me.dylancurzon.dontdie.gfx.window;

public abstract class WindowKeyEvent {

    public static class Press extends WindowKeyEvent {
        public Press(int code) {
            super(code);
        }
    }

    public static class Release extends WindowKeyEvent {
        public Release(int code) {
            super(code);
        }
    }

    public static class Repeat extends WindowKeyEvent {
        public Repeat(int code) {
            super(code);
        }
    }

    private int code;

    private WindowKeyEvent(int code) {
        this.code = code;
    }

    public int getKeyCode() {
        return code;
    }

}

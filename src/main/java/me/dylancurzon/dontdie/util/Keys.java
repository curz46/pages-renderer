package me.dylancurzon.dontdie.util;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;

public class Keys {

    private static boolean[] keys = new boolean[GLFW_KEY_LAST];

    public static void press(int code) {
        keys[code] = true;
    }

    public static void release(int code) {
        keys[code] = false;
    }

    public static boolean isPressed(int code) {
        return keys[code];
    }

}

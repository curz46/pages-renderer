package me.dylancurzon.dontdie;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {

    public static void main(final String[] args) {
        // Taking reference from https://github.com/LWJGL/lwjgl3-wiki/wiki/2.2.-OpenGL

        // Make errors print to stderr
        GLFWErrorCallback errorCallback;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        // Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Create the Display.
        final String title = "Don't Die";
        // TODO: Consider these values more carefully; this is 4:3 and nobody likes that.
        final int width = 1024;
        final int height = 768;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        // TODO: The window *should* be resizable, but content will have to scale appropriately. For now, leave it off.
        // Note: This *is* done automatically by OpenGL, but it will cause content to distort should the aspect ratio
        // not be maintained.
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        final long id = glfwCreateWindow(width, height, title, NULL, NULL); //Does the actual window creation
        if (id == NULL) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(id);
        // TODO: I'm paranoid that making this true will result in terrifying errors in the future.
        GL.createCapabilities(true);

        glfwSwapInterval(1);
        glfwShowWindow(id);
    }

}

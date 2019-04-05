package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RenderContext {

    private long window;
    private int tileShaderProgram;

//    private int buffer;
    private VertexBuffer triangle;

    /**
     * Initialise OpenGL, the Display and any Shaders.
     * @return The ID of the Window.
     */
    public long init() {
        // Taking reference from https://github.com/LWJGL/lwjgl3-wiki/wiki/2.2.-OpenGL

        // Make errors print to stderr
        GLFWErrorCallback errorCallback;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        // Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = createWindow();

        glfwMakeContextCurrent(window);
        // TODO: I'm paranoid that making this true will result in terrifying errors in the future.
        GL.createCapabilities(true);

        glfwSwapInterval(1);
        glfwShowWindow(window);
        glClearColor(0, 0, 0, 0);

        createShaders();
        initRender();

        return window;
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

        float[] positions = {
            -0.5f, -0.5f,
            0.0f, 0.5f,
            0.5f, -0.5f
        };

        ARBShaderObjects.glUseProgramObjectARB(tileShaderProgram);
//        glBindBuffer(GL_ARRAY_BUFFER, this.buffer);
//        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
//
        triangle.bind();
        triangle.upload(positions);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        VertexBuffer.unbind();

        glDrawArrays(GL_TRIANGLES, 0, 3);

        ARBShaderObjects.glUseProgramObjectARB(0);

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void initRender() {
        glClearColor(0f, 0f, 0f, 0f);

//        this.buffer = glGenBuffers();
        triangle = VertexBuffer.make();
    }

    private long createWindow() {
        // Create the Display.
        String title = "Don't Die";
        // TODO: Consider these values more carefully; this is 4:3 and nobody likes that.
        int width = 1024;
        int height = 768;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        // TODO: The window *should* be resizable, but content will have to scale appropriately. For now, leave it off.
        // Note: This *is* done automatically by OpenGL, but it will cause content to distort should the aspect ratio
        // not be maintained.
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        long id = glfwCreateWindow(width, height, title, NULL, NULL); //Does the actual window creation
        if (id == NULL) throw new RuntimeException("Failed to create window");

        return id;
    }

    private void createShaders() {
        tileShaderProgram = createShaderProgram("tiles");
    }

    /**
     * Create a Shader Program with a vertex and fragment shader, located at "shaders/{name}.vert|frag".
     * @param name The name to use when locating the shaders.
     * @return The ID of the Shader Program created.
     */
    private int createShaderProgram(String name) {
        int vertShader = createShader("shaders/" + name + ".vert",
            ARBVertexShader.GL_VERTEX_SHADER_ARB);
        int fragShader = createShader("shaders/" + name + ".frag",
            ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        int program = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(program, vertShader);
        ARBShaderObjects.glAttachObjectARB(program, fragShader);

        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB)
            == GL_FALSE) {
            throw new RuntimeException("Link failed for shader program: " + name);
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB)
            == GL11.GL_FALSE) {
            throw new RuntimeException("Validate failed for shader program: " + name);
        }

        return program;
    }

    private int createShader(String path, int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) return 0;

            ARBShaderObjects.glShaderSourceARB(shader, readFile(path));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB)
                == GL_FALSE) {
                // Get the error log for this shader
                String info = ARBShaderObjects.glGetInfoLogARB(
                    shader,
                    ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
                throw new RuntimeException(
                    "An error occurred creating the shader [path=" + path + ", type=" + shaderType + "]:" + info);
            }

            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    /**
     * Takes a File path and attempts to read it, line by line, into a String.
     * @param path The path of the File to read.
     * @return The contents of the File.
     */
    private String readFile(String path) {
        StringBuilder result = new StringBuilder();
        ClassLoader loader = getClass().getClassLoader();

        try (Scanner scanner = new Scanner(loader.getResourceAsStream(path))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while attempting to read File of path: " + path);
        }

        return result.toString();
    }

}

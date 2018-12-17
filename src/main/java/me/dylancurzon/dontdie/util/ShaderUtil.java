package me.dylancurzon.dontdie.util;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import java.util.Scanner;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;

public class ShaderUtil {

    /**
     * Create a Shader Program with a vertex and fragment shader, located at "shaders/{name}.vert|frag".
     * @param name The name to use when locating the shaders.
     * @return The ID of the Shader Program created.
     */
    public static int createShaderProgram(final String name) {
        final int vertShader = createShader("shaders/" + name + ".vert",
            ARBVertexShader.GL_VERTEX_SHADER_ARB);
        final int fragShader = createShader("shaders/" + name + ".frag",
            ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        final int program = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(program, vertShader);
        ARBShaderObjects.glAttachObjectARB(program, fragShader);

        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB)
            == GL_FALSE) {
            final String log = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));
            throw new RuntimeException("Link failed for shader program: " + log);
        }

        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB)
            == GL11.GL_FALSE) {
            throw new RuntimeException("Validate failed for shader program: " + name);
        }

        return program;
    }

    public static int createShader(final String path, final int shaderType) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
            if (shader == 0) return 0;

            ARBShaderObjects.glShaderSourceARB(shader, readFile(path));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB)
                == GL_FALSE) {
                // Get the error log for this shader
                final String info = ARBShaderObjects.glGetInfoLogARB(
                    shader,
                    ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
                throw new RuntimeException(
                    "An error occurred creating the shader [path=" + path + ", type=" + shaderType + "]:" + info);
            }

            return shader;
        } catch (final Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    /**
     * Takes a File path and attempts to read it, line by line, into a String.
     * @param path The path of the File to read.
     * @return The contents of the File.
     */
    private static String readFile(final String path) {
        final StringBuilder result = new StringBuilder();
        final ClassLoader loader = ShaderUtil.class.getClassLoader();

        try (final Scanner scanner = new Scanner(loader.getResourceAsStream(path))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        } catch (final Exception e) {
            throw new RuntimeException("An exception occurred while attempting to read File of path: " + path);
        }

        return result.toString();
    }

}

package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.util.ByteBuf;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Sprite {

    public static Sprite loadSprite(final String name) {
        final String filename = "textures/" + name + ".png";
        return Sprite.loadSprite(Sprite.class.getClassLoader().getResourceAsStream(filename));
    }

    public static Sprite loadSprite(final InputStream in) {
//        try {
//            final BufferedImage image = ImageIO.read(in);
//            return new Sprite(
//                image.getWidth(),
//                image.getHeight(),
//                image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), (int[]) null)
//            );
//        } catch (final IOException e) {
//            throw new RuntimeException("An exception occurred while reading file: ", e);
//        }

        try {
            final PNGDecoder decoder = new PNGDecoder(in);

            int width = decoder.getWidth();
            int height = decoder.getHeight();

            ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
            decoder.decode(buf, width * 4, PNGDecoder.Format.RGBA);

            buf.flip();
            return new Sprite(width, height, buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Sprite testSprite(final AnimatedSprite anim) {
        final int width = anim.getWidth();
        final int height = anim.getHeight();
        final ByteBuffer buf = anim.getFrames()[0];
        return new Sprite(width, height, buf);
    }

    private final int width;
    private final int height;
//    private final int[] pixels;
    private final ByteBuffer buf;

    public Sprite(final int width, final int height, final ByteBuffer buf) {
        this.width = width;
        this.height = height;
        this.buf = buf;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getBuffer() {
        return this.buf;
    }

}

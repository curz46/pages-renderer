package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class OldSprite {

    public static OldSprite loadSprite(final String name) {
        final String filename = "textures/" + name + ".png";
        return OldSprite.loadSprite(OldSprite.class.getClassLoader().getResourceAsStream(filename));
    }

    public static OldSprite loadSprite(final InputStream in) {
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
            return new OldSprite(width, height, buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final int width;
    protected final int height;
//    private final int[] pixels;
    protected final ByteBuffer buffer;

    public OldSprite(final int width, final int height, final ByteBuffer buffer) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

}

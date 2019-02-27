package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AnimatedSprite {

    private final int width;
    private final int height;
    private final int frameCount;
    private final ByteBuffer[] frames;

    public static AnimatedSprite loadAnimatedSprite(final String name, final int frameCount) {
        // Assume that this texture is located at textures/<name>.png
        try {
            final InputStream in = AnimatedSprite.class.getClassLoader()
                .getResourceAsStream("textures/" + name + ".png");

            final PNGDecoder decoder = new PNGDecoder(in);

            int width = decoder.getWidth();
            int imageHeight = decoder.getHeight();

            ByteBuffer pixelBuffer = ByteBuffer.wrap(new byte[4 * width * imageHeight]);
            decoder.decode(pixelBuffer, width * 4, PNGDecoder.Format.RGBA);
            final byte[] pixels = pixelBuffer.array();

            if (imageHeight % frameCount != 0) {
                throw new RuntimeException("AnimatedSprite is malformed; not a multiple of frameCount");
            }
            int height = imageHeight / frameCount;

            final ByteBuffer[] frames = new ByteBuffer[frameCount];
            for (int i = 0; i < frameCount; i++) {
                ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);

                final int initialY = i * height;
                final int indexBegin = initialY * width * 4;
                final int indexEnd = indexBegin + (width * height * 4);

                for (int j = indexBegin; j < indexEnd; j++) {
                    buf.put(pixels[j]);
                }

                buf.flip();
                frames[i] = buf;
            }

            return new AnimatedSprite(width, height, frameCount, frames);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AnimatedSprite(final int width, final int height, final int frameCount, final ByteBuffer[] frames) {
        this.width = width;
        this.height = height;
        this.frameCount = frameCount;
        this.frames = frames;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public ByteBuffer[] getFrames() {
        return this.frames;
    }

}

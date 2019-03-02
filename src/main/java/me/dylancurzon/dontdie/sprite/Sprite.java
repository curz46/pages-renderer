package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.util.ByteBuf;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Sprite {

    private final int width;
    private final int height;
    private final int frameCount;
    private final ByteBuffer[] frames;

    public static Sprite loadSprite(final String name) {
        return Sprite.loadAnimatedSprite(name, 1);
    }

    public static Sprite loadAnimatedSprite(final String name, final int frameCount) {
        // Assume that this texture is located at textures/<name>.png
        try {
            final InputStream in = Sprite.class.getClassLoader()
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
//                ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
                final ByteBuffer buf = ByteBuffer.wrap(new byte[4 * width * height]);

                final int initialY = i * height;
                final int indexBegin = initialY * width * 4;
                final int indexEnd = indexBegin + (width * height * 4);

                for (int j = indexBegin; j < indexEnd; j++) {
                    buf.put(pixels[j]);
                }

                buf.flip();
                frames[i] = buf;
            }

            return new Sprite(width, height, frameCount, frames);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sprite(final int width, final int height, final int frameCount, final ByteBuffer[] frames) {
        this.width = width;
        this.height = height;
        this.frameCount = frameCount;
        this.frames = frames;
    }

    public TickableSprite createTickableSprite() {
        return new TickableSprite();
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

    public class TickableSprite implements Tickable {

        private int ticks;

        @Override
        public void tick() {
            this.ticks++;
        }

        public ByteBuffer getCurrentFrame() {
            return Sprite.this.frames[this.ticks % Sprite.this.frameCount];
        }

        public int getTicks() {
            return this.ticks;
        }

        public Sprite getAnimatedSprite() {
            return Sprite.this;
        }

    }

}

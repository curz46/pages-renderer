package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.util.Buffers;
import me.dylancurzon.dontdie.util.ByteBuf;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Sprite {

    protected final int width;
    protected final int height;
    protected final int frameCount;
    protected final byte[][] frames;

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

//            ByteBuffer pixelBuffer = ByteBuffer.wrap(new byte[4 * width * imageHeight]);
            final ByteBuffer spriteBuffer = BufferUtils.createByteBuffer(width * imageHeight * 4);
            decoder.decode(spriteBuffer, width * 4, PNGDecoder.Format.RGBA);
            spriteBuffer.flip();

            final byte[] pixels = Buffers.asByteArray(spriteBuffer);

            if (imageHeight % frameCount != 0) {
                throw new RuntimeException("AnimatedSprite is malformed; not a multiple of frameCount");
            }
            int height = imageHeight / frameCount;

            final byte[][] frames = new byte[frameCount][width * imageHeight * 4];
            for (int frameNum = 0; frameNum < frameCount; frameNum++) {
//                ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
//                final ByteBuffer buf = ByteBuffer.wrap(new byte[4 * width * height]);
                final byte[] frame = new byte[width * height * 4];

//                final int initialY = i * height;
//                final int indexBegin = initialY * width * 4;
//                final int indexEnd = indexBegin + (width * height * 4);

//                for (int j = indexBegin; j < indexEnd; j++) {
//                    buf.put(pixels[j]);
//                }

                for (int dx = 0; dx < width; dx++) {
                    for (int dy = 0; dy < height; dy++) {
                        final int xp = dx;
                        final int yp = dy + frameNum * height;
                        for (int b = 0; b < 4; b++) {
                            frame[(dx + dy * width) * 4 + b] = pixels[(xp + yp * imageHeight) * 4 + b];
                        }
                    }
                }

//                buf.flip();
//                frames[i] = buf;
                frames[frameNum] = frame;
            }

            return new Sprite(width, height, frameCount, frames);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sprite(final int width, final int height, final int frameCount, final byte[][] frames) {
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

    public byte[][] getFrames() {
        return this.frames;
    }

    public class TickableSprite implements Tickable {

        private int ticks;

        @Override
        public void tick() {
            this.ticks++;
        }

        public byte[] getCurrentFrame() {
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

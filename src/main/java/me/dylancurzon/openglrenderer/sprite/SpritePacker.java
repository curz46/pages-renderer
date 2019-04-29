package me.dylancurzon.openglrenderer.sprite;

import me.dylancurzon.pages.util.Vector2i;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// A Java implementation of the following algorithm:
// https://github.com/TeamHypersomnia/rectpack2D#algorithm
public class SpritePacker {

    // OpenGL safe Texture size
    // TODO: We may want to adjust this based on content if the large Texture is causing performance issues.
    private static final int WIDTH = 2048;
    private static final int HEIGHT = 2048;

    private final Set<Sprite> sprites;
    private Map<Sprite, Vector2i> spriteMap = new LinkedHashMap<>();

    private byte[] pixels;

    public SpritePacker(Set<Sprite> sprites) {
        this.sprites = sprites;
        pack(sprites);
    }

    public Optional<Vector2i> getSpritePosition(Sprite sprite) {
        return Optional.ofNullable(spriteMap.get(sprite));
    }

    public byte[] getPixels() {
        return pixels;
    }

    public Map<Sprite, Vector2i> getSpriteMap() {
        return spriteMap;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    private void pack(Set<Sprite> sprites) {
        List<Image> images = sprites.stream()
            .map(Image::new)
            .sorted(Comparator.comparingInt(a -> a.width * a.height))
            .collect(Collectors.toList());
        Collections.reverse(images);

        LinkedList<Bin> bins = new LinkedList<>();
        bins.add(new Bin(0, 0, WIDTH, HEIGHT));

        for (Image image : images) {
            int imageWidth = image.width;
            int imageHeight = image.height;
            ListIterator<Bin> iterator = bins.listIterator(bins.size());

            boolean success = false;

            // First, find a Bin that this Sprite is able to fit into
            // We do this in reverse order in order to prioritize smaller splits
            while (iterator.hasPrevious()) {
                Bin bin = iterator.previous();

                // Calculate the bigger and smaller split as shown here:
                // https://github.com/TeamHypersomnia/rectpack2D/blob/master/images/diag01.png

                // This addresses corner cases using the following as reference:
                // https://github.com/TeamHypersomnia/rectpack2D/blob/master/src/insert_and_split.h

                int freeWidth = bin.width - imageWidth;
                int freeHeight = bin.height - imageHeight;

                if (freeWidth < 0 || freeHeight < 0) {
                    // Sprite cannot fit in this Bin, as dimensions are greater
                    continue;
                }

                if (freeWidth == 0 && freeHeight == 0) {
                    // Sprite perfectly fits, so delete the space and create no splits
                    iterator.remove();
                    spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;
                    break;
                }

                // Conditions for when there would only be one split

                if (freeWidth > 0 && freeHeight == 0) {
                    int splitX = bin.x + imageWidth;
                    int splitY = bin.y;
                    int splitWidth = bin.width - imageWidth;
                    int splitHeight = bin.height;
                    iterator.remove();
                    bins.addLast(new Bin(splitX, splitY, splitWidth, splitHeight));
                    spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;
                    break;
                }

                if (freeWidth == 0) {
                    int splitX = bin.x;
                    int splitY = bin.y + imageHeight;
                    int splitWidth = bin.width;
                    int splitHeight = bin.height - imageHeight;
                    iterator.remove();
                    bins.addLast(new Bin(splitX, splitY, splitWidth, splitHeight));
                    spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;
                    break;
                }

                if (freeWidth > freeHeight) {
                    int biggerX = bin.x + imageWidth;
                    int biggerY = bin.y;
                    int biggerWidth = freeWidth;
                    int biggerHeight = bin.height;

                    int smallerX = bin.x;
                    int smallerY = bin.y + imageHeight;
                    int smallerWidth = image.width;
                    int smallerHeight = freeHeight;

                    iterator.remove();
                    // Push to bins
                    bins.addLast(new Bin(biggerX, biggerY, biggerWidth, biggerHeight));
                    bins.addLast(new Bin(smallerX, smallerY, smallerWidth, smallerHeight));
                    spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;

                    break;
                }

                int biggerX = bin.x;
                int biggerY = bin.y + imageHeight;
                int biggerWidth = bin.width;
                int biggerHeight = freeHeight;

                int smallerX = bin.x + imageWidth;
                int smallerY = bin.y;
                int smallerWidth = freeWidth;
                int smallerHeight = imageHeight;

                iterator.remove();
                // Push to bins
                bins.addLast(new Bin(biggerX, biggerY, biggerWidth, biggerHeight));
                bins.addLast(new Bin(smallerX, smallerY, smallerWidth, smallerHeight));
                spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                success = true;
                break;
            }

            if (!success) {
                System.out.println("Failed to pack Sprite: " + image);
            }
        }

        pixels = new byte[WIDTH * HEIGHT * 4];
        for (Map.Entry<Sprite, Vector2i> entry : spriteMap.entrySet()) {
            Sprite sprite = entry.getKey();
            Vector2i position = entry.getValue();

            for (int frameNum = 0; frameNum < sprite.getFrameCount(); frameNum++) {
                byte[] frame = sprite.getFrames()[frameNum];

                for (int xd = 0; xd < sprite.getWidth(); xd++) {
                    for (int yd = 0; yd < sprite.getHeight(); yd++) {
                        int xa = xd + position.getX();
                        int ya = yd + position.getY();
                        //rgba
                        pixels[(xa + ya * WIDTH) * 4 + 0] = frame[(xd + yd * sprite.getWidth()) * 4 + 0];
                        pixels[(xa + ya * WIDTH) * 4 + 1] = frame[(xd + yd * sprite.getWidth()) * 4 + 1];
                        pixels[(xa + ya * WIDTH) * 4 + 2] = frame[(xd + yd * sprite.getWidth()) * 4 + 2];
                        pixels[(xa + ya * WIDTH) * 4 + 3] = frame[(xd + yd * sprite.getWidth()) * 4 + 3];
                    }
                }
            }
        }

//        final SpritePacker packer = new SpritePacker(Sprites.getSprites());
//        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

//        DataBuffer dataBuffer = new DataBufferByte(pixels, pixels.length);
//
////3 bytes per pixel: red, green, blue
//        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, WIDTH, HEIGHT, 4 * WIDTH, 4, new int[] {0, 1, 2, 3}, (Point)null);
//        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), true, false, Transparency.BITMASK, DataBuffer.TYPE_BYTE);
//        BufferedImage image = new BufferedImage(cm, raster, true, null);
//
//        try {
//            ImageIO.write(image, "png", new File(ThreadLocalRandom.current().nextInt(1000) + "output.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    class Image {

        Sprite sprite;
        int width;
        int height;

        /**
         * Accounts for animated Sprites by considering it as a long image
         */
        public Image(Sprite sprite) {
            this.sprite = sprite;
            width = sprite.getWidth();
            height = sprite.getHeight();
        }

    }

    class Bin {

        int x;
        int y;
        int width;
        int height;

        public Bin(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return "Bin{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height + "}";
        }

    }

}

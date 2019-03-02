package me.dylancurzon.dontdie.sprite;

import me.dylancurzon.dontdie.util.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

// A Java implementation of the following algorithm:
// https://github.com/TeamHypersomnia/rectpack2D#algorithm
public class SpritePacker {

    // OpenGL safe Texture size
    // TODO: We may want to adjust this based on content if the large Texture is causing performance issues.
    private static final int WIDTH = 2018;
    private static final int HEIGHT = 2018;

    private final Set<Sprite> sprites;
    private Map<Sprite, Vector2i> spriteMap = new HashMap<>();

    public SpritePacker(final Set<Sprite> sprites) {
        this.sprites = sprites;
        this.pack(sprites);
    }

    public Optional<Vector2i> getSpritePosition(final Sprite sprite) {
        return Optional.ofNullable(this.spriteMap.get(sprite));
    }

    public Map<Sprite, Vector2i> getSpriteMap() {
        return this.spriteMap;
    }

    private void pack(final Set<Sprite> sprites) {
        final List<Image> images = sprites.stream()
            .map(Image::new)
            .sorted(Comparator.comparingInt(a -> a.width * a.height))
            .collect(Collectors.toList());
        Collections.reverse(images);

        final LinkedList<Bin> bins = new LinkedList<>();
        bins.add(new Bin(0, 0, WIDTH, HEIGHT));

        for (final Image image : images) {
            final int imageWidth = image.width;
            final int imageHeight = image.height;
            final ListIterator<Bin> iterator = bins.listIterator(bins.size());

            boolean success = false;

            // First, find a Bin that this Sprite is able to fit into
            // We do this in reverse order in order to prioritize smaller splits
            while (iterator.hasPrevious()) {
                final Bin bin = iterator.previous();

                // Calculate the bigger and smaller split as shown here:
                // https://github.com/TeamHypersomnia/rectpack2D/blob/master/images/diag01.png

                // This addresses corner cases using the following as reference:
                // https://github.com/TeamHypersomnia/rectpack2D/blob/master/src/insert_and_split.h

                final int freeWidth = bin.width - imageWidth;
                final int freeHeight = bin.height - imageHeight;

                if (freeWidth < 0 || freeHeight < 0) {
                    // Sprite cannot fit in this Bin, as dimensions are greater
                    continue;
                }

                if (freeWidth == 0 && freeHeight == 0) {
                    // Sprite perfectly fits, so delete the space and create no splits
                    iterator.remove();
                    this.spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;
                    break;
                }

                // Conditions for when there would only be one split

                if (freeWidth > 0 && freeHeight == 0) {
                    final int splitX = bin.x + imageWidth;
                    final int splitY = bin.y;
                    final int splitWidth = bin.width - imageWidth;
                    final int splitHeight = bin.height;
                    iterator.remove();
                    bins.addLast(new Bin(splitX, splitY, splitWidth, splitHeight));
                    this.spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;
                    break;
                }

                if (freeWidth == 0) {
                    final int splitX = bin.x;
                    final int splitY = bin.y + imageHeight;
                    final int splitWidth = bin.width;
                    final int splitHeight = bin.height - imageHeight;
                    iterator.remove();
                    bins.addLast(new Bin(splitX, splitY, splitWidth, splitHeight));
                    this.spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;
                    break;
                }

                if (freeWidth > freeHeight) {
                    final int biggerX = bin.x + imageWidth;
                    final int biggerY = bin.y;
                    final int biggerWidth = freeWidth;
                    final int biggerHeight = bin.height;

                    final int smallerX = bin.x;
                    final int smallerY = bin.y + imageHeight;
                    final int smallerWidth = image.width;
                    final int smallerHeight = freeHeight;

                    iterator.remove();
                    // Push to bins
                    bins.addLast(new Bin(biggerX, biggerY, biggerWidth, biggerHeight));
                    bins.addLast(new Bin(smallerX, smallerY, smallerWidth, smallerHeight));
                    this.spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                    success = true;

                    break;
                }

                final int biggerX = bin.x;
                final int biggerY = bin.y + imageHeight;
                final int biggerWidth = bin.width;
                final int biggerHeight = freeHeight;

                final int smallerX = bin.x + imageWidth;
                final int smallerY = bin.y;
                final int smallerWidth = freeWidth;
                final int smallerHeight = imageHeight;

                iterator.remove();
                // Push to bins
                bins.addLast(new Bin(biggerX, biggerY, biggerWidth, biggerHeight));
                bins.addLast(new Bin(smallerX, smallerY, smallerWidth, smallerHeight));
                this.spriteMap.put(image.sprite, Vector2i.of(bin.x, bin.y));
                success = true;
                break;
            }

            if (!success) {
                System.out.println("Failed to pack Sprite: " + image);
            }
        }
    }

    class Image {

        Sprite sprite;
        int width;
        int height;

        /**
         * Accounts for animated Sprites by considering it as a long image
         */
        public Image(final Sprite sprite) {
            this.sprite = sprite;
            this.width = sprite.getWidth() * sprite.getFrameCount();
            this.height = sprite.getHeight();
        }

    }

    class Bin {

        int x;
        int y;
        int width;
        int height;

        public Bin(final int x, final int y, final int width, final int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

    }

}

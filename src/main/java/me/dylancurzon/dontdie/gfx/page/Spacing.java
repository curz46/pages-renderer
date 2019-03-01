package me.dylancurzon.dontdie.gfx.page;

public class Spacing {

    public static final Spacing ZERO = Spacing.of(0);

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    private Spacing(final int left, final int top, final int right, final int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public static Spacing of(final int margin) {
        return new Spacing(margin, margin, margin, margin);
    }

    public static Spacing of(final int marginX, final int marginY) {
        return new Spacing(marginX, marginY, marginX, marginY);
    }

    public static Spacing of(final int marginLeft, final int marginTop, final int marginRight,
                             final int marginBottom) {
        return new Spacing(marginLeft, marginTop, marginRight, marginBottom);
    }

    public int getLeft() {
        return this.left;
    }

    public int getTop() {
        return this.top;
    }

    public int getRight() {
        return this.right;
    }

    public int getBottom() {
        return this.bottom;
    }

}

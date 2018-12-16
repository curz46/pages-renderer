package me.dylancurzon.dontdie.util;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class Vector2i {

    @NotNull
    private final int x;
    @NotNull
    private final int y;

    public Vector2i(@NotNull final int x, @NotNull final int y) {
        this.x = x;
        this.y = y;
    }

    @NotNull
    public static Vector2i of(@NotNull final int x, @NotNull final int y) {
        return new Vector2i(x, y);
    }

    @NotNull
    public Vector2i setX(final int value) {
        return new Vector2i(value, this.y);
    }

    @NotNull
    public Vector2i setY(final int value) {
        return new Vector2i(this.x, value);
    }

    @NotNull
    public Vector2i add(@NotNull final Vector2i addend) {
        return new Vector2i(
            this.x + addend.getX(),
            this.y + addend.getY()
        );
    }

    @NotNull
    public Vector2i add(@NotNull final int addend) {
        return new Vector2i(
            this.x + addend,
            this.y + addend
        );
    }

    @NotNull
    public Vector2i sub(@NotNull final Vector2i subtrahend) {
        return new Vector2i(
            this.x - subtrahend.getX(),
            this.y - subtrahend.getY()
        );
    }

    @NotNull
    public Vector2i sub(@NotNull final int subtrahend) {
        return new Vector2i(
            this.x - subtrahend,
            this.y - subtrahend
        );
    }

    @NotNull
    public Vector2i mul(@NotNull final Vector2i factor) {
        return new Vector2i(
            this.x * factor.getX(),
            this.y * factor.getY()
        );
    }

    @NotNull
    public Vector2i mul(@NotNull final int factor) {
        return new Vector2i(
            this.x * factor,
            this.y * factor
        );
    }

    @NotNull
    public Vector2i integerDiv(@NotNull final Vector2i divisor) {
        return new Vector2i(
            this.x / divisor.getX(),
            this.y / divisor.getY()
        );
    }

    @NotNull
    public Vector2i integerDiv(@NotNull final Vector2d divisor) {
        return this.integerDiv(divisor.toInt());
    }

    @NotNull
    public Vector2i integerDiv(@NotNull final int divisor) {
        return new Vector2i(
            this.x / divisor,
            this.y / divisor
        );
    }

    @NotNull
    public Vector2d div(@NotNull final Vector2d divisor) {
        return new Vector2d(
            ((double) this.x) / divisor.getX(),
            ((double) this.y) / divisor.getY()
        );
    }

    @NotNull
    public Vector2d div(@NotNull final double divisor) {
        return new Vector2d(
            ((double) this.x) / divisor,
            ((double) this.y) / divisor
        );
    }

    @NotNull
    public Vector2i abs() {
        return new Vector2i(
            Math.abs(this.x),
            Math.abs(this.y)
        );
    }

    @NotNull
    public Vector2d normalize() {
        final double value = this.absv();
        return new Vector2d(
            ((double) this.x) / value,
            ((double) this.y) / value
        );
    }

    @NotNull
    public double absv() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    @NotNull
    public Vector2d toDouble() {
        return new Vector2d(
            this.x,
            this.y
        );
    }

    @NotNull
    public int getX() {
        return this.x;
    }

    @NotNull
    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Vector2i) {
            final Vector2i vector = (Vector2i) object;
            return this.x == vector.getX() && this.y == vector.getY();
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return this.x < this.y
            ? this.y * this.y + this.x
            : this.x * this.x + this.x + this.y;

    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.x, this.y);
    }

}

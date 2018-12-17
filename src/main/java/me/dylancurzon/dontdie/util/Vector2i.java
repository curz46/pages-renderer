package me.dylancurzon.dontdie.util;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class Vector2i {

    private final int x;
    private final int y;

    public Vector2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2i of(final int x, final int y) {
        return new Vector2i(x, y);
    }

    public Vector2i setX(final int value) {
        return new Vector2i(value, this.y);
    }

    public Vector2i setY(final int value) {
        return new Vector2i(this.x, value);
    }

    public Vector2i add(final Vector2i addend) {
        return new Vector2i(
            this.x + addend.getX(),
            this.y + addend.getY()
        );
    }

    public Vector2i add(final int addend) {
        return new Vector2i(
            this.x + addend,
            this.y + addend
        );
    }

    public Vector2i sub(final Vector2i subtrahend) {
        return new Vector2i(
            this.x - subtrahend.getX(),
            this.y - subtrahend.getY()
        );
    }

    public Vector2i sub(final int subtrahend) {
        return new Vector2i(
            this.x - subtrahend,
            this.y - subtrahend
        );
    }

    public Vector2i mul(final Vector2i factor) {
        return new Vector2i(
            this.x * factor.getX(),
            this.y * factor.getY()
        );
    }

    public Vector2i mul(final int factor) {
        return new Vector2i(
            this.x * factor,
            this.y * factor
        );
    }

    public Vector2i integerDiv(final Vector2i divisor) {
        return new Vector2i(
            this.x / divisor.getX(),
            this.y / divisor.getY()
        );
    }

    public Vector2i integerDiv(final Vector2d divisor) {
        return this.integerDiv(divisor.toInt());
    }

    public Vector2i integerDiv(final int divisor) {
        return new Vector2i(
            this.x / divisor,
            this.y / divisor
        );
    }

    public Vector2d div(final Vector2d divisor) {
        return new Vector2d(
            ((double) this.x) / divisor.getX(),
            ((double) this.y) / divisor.getY()
        );
    }

    public Vector2d div(final double divisor) {
        return new Vector2d(
            ((double) this.x) / divisor,
            ((double) this.y) / divisor
        );
    }

    public Vector2i abs() {
        return new Vector2i(
            Math.abs(this.x),
            Math.abs(this.y)
        );
    }

    public Vector2d normalize() {
        final double value = this.absv();
        return new Vector2d(
            ((double) this.x) / value,
            ((double) this.y) / value
        );
    }

    public double absv() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    public Vector2d toDouble() {
        return new Vector2d(
            this.x,
            this.y
        );
    }

    public int getX() {
        return this.x;
    }

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

package me.dylancurzon.dontdie.util;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class Vector2d {

    protected final double x;
    protected final double y;

    public Vector2d(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2d of(final double x, final double y) {
        return new Vector2d(x, y);
    }

    public Vector2d add(final Vector2d addend) {
        return new Vector2d(
            this.x + addend.getX(),
            this.y + addend.getY()
        );
    }

    public Vector2d add(final double addend) {
        return new Vector2d(
            this.x + addend,
            this.y + addend
        );
    }

    public Vector2d sub(final Vector2d subtrahend) {
        return new Vector2d(
            this.x - subtrahend.getX(),
            this.y - subtrahend.getY()
        );
    }

    public Vector2d sub(final double subtrahend) {
        return new Vector2d(
            this.x - subtrahend,
            this.y - subtrahend
        );
    }

    public Vector2d mul(final Vector2d factor) {
        return new Vector2d(
            this.x * factor.getX(),
            this.y * factor.getY()
        );
    }

    public Vector2d mul(final double factor) {
        return new Vector2d(
            this.x * factor,
            this.y * factor
        );
    }

    public Vector2d div(final Vector2d divisor) {
        return new Vector2d(
            this.x / divisor.getX(),
            this.y / divisor.getY()
        );
    }

    public Vector2d div(final double divisor) {
        return new Vector2d(
            this.x / divisor,
            this.y / divisor
        );
    }

    public Vector2d floor() {
        return new Vector2d(
            Math.floor(this.x),
            Math.floor(this.y)
        );
    }

    /**
     * @return Vector with each component rounded to the nearest integer which is the closest to
     * zero.
     */
    public Vector2d floorAbs() {
        return new Vector2d(
            this.x >= 0 ? Math.floor(this.x) : Math.ceil(this.x),
            this.y >= 0 ? Math.floor(this.y) : Math.ceil(this.y)
        );
    }

    public Vector2d ceil() {
        return new Vector2d(
            Math.ceil(this.x),
            Math.ceil(this.y)
        );
    }

    /**
     * @return Vector with each component rounded to the nearest integer which is furthest away
     * from zero.
     */
    public Vector2d ceilAbs() {
        return new Vector2d(
            this.x >= 0 ? Math.ceil(this.x) : Math.floor(this.x),
            this.y >= 0 ? Math.ceil(this.y) : Math.floor(this.y)
        );
    }

    public Vector2d abs() {
        return new Vector2d(
            Math.abs(this.x),
            Math.abs(this.y)
        );
    }

    public Vector2i toInt() {
        return new Vector2i(
            (int) this.x,
            (int) this.y
        );
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof Vector2d) {
            final Vector2d vector = (Vector2d) object;
            return this.x == vector.getX() && this.y == vector.getY();
        }
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return (int) Math.pow(this.x * 0x1f1f1f1f, this.y);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", this.x, this.y);
    }

}

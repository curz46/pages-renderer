package me.dylancurzon.dontdie.util;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class Vector2f {

    protected final float x;
    protected final float y;

    public Vector2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2f of(final float x, final float y) {
        return new Vector2f(x, y);
    }

    public Vector2f add(final Vector2f addend) {
        return new Vector2f(
            this.x + addend.getX(),
            this.y + addend.getY()
        );
    }

    public Vector2f add(final float addend) {
        return new Vector2f(
            this.x + addend,
            this.y + addend
        );
    }

    public Vector2f sub(final Vector2f subtrahend) {
        return new Vector2f(
            this.x - subtrahend.getX(),
            this.y - subtrahend.getY()
        );
    }

    public Vector2f sub(final float subtrahend) {
        return new Vector2f(
            this.x - subtrahend,
            this.y - subtrahend
        );
    }

    public Vector2f mul(final Vector2f factor) {
        return new Vector2f(
            this.x * factor.getX(),
            this.y * factor.getY()
        );
    }

    public Vector2f mul(final float factor) {
        return new Vector2f(
            this.x * factor,
            this.y * factor
        );
    }

    public Vector2f div(final Vector2f divisor) {
        return new Vector2f(
            this.x / divisor.getX(),
            this.y / divisor.getY()
        );
    }

    public Vector2f div(final float divisor) {
        return new Vector2f(
            this.x / divisor,
            this.y / divisor
        );
    }

    public Vector2f floor() {
        return new Vector2f(
            (float) Math.floor(this.x),
            (float) Math.floor(this.y)
        );
    }

    /**
     * @return Vector with each component rounded to the nearest integer which is the closest to
     * zero.
     */
    public Vector2f floorAbs() {
        return new Vector2f(
            this.x >= 0 ? (float) Math.floor(this.x) : (float) Math.ceil(this.x),
            this.y >= 0 ? (float) Math.floor(this.y) : (float) Math.ceil(this.y)
        );
    }

    public Vector2f ceil() {
        return new Vector2f(
            (float) Math.ceil(this.x),
            (float) Math.ceil(this.y)
        );
    }

    /**
     * @return Vector with each component rounded to the nearest integer which is furthest away
     * from zero.
     */
    public Vector2f ceilAbs() {
        return new Vector2f(
            this.x >= 0 ? (float) Math.ceil(this.x) : (float) Math.floor(this.x),
            this.y >= 0 ? (float) Math.ceil(this.y) : (float) Math.floor(this.y)
        );
    }

    public Vector2f abs() {
        return new Vector2f(
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

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof Vector2f) {
            final Vector2f vector = (Vector2f) object;
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

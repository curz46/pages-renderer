package me.dylancurzon.dontdie.gfx.page.animation;

public class SineEaseInAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public SineEaseInAnimation(final double min, final double max, final int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        // When ticks=0, this will equal 0. As ticks=duration, this will equal Math.PI / 2.
        final double x = (((double) super.ticks) / super.duration) * (Math.PI / 2);
        // Between 0 and Math.PI / 2, the sine function will return a value between 0 and 1. The
        // proceeding calculation expands and offsets this range to the desired minimum and maximum
        // values.
        return (super.max - super.min) * Math.sin(x) + super.min;
    }

}

package me.dylancurzon.dontdie.gfx.page.animation;

public class SineEaseOutAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public SineEaseOutAnimation(final double min, final double max, final int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        final double x = (((double) super.ticks) / super.duration) * (Math.PI / 2);
        return 1 + Math.sin(x - (Math.PI / 2));
    }

}

package me.dylancurzon.dontdie.gfx.page.animation;

public class QuarticEaseOutAnimation extends Animation {

    /**
     * @see Animation for information on this class' parameters.
     */
    public QuarticEaseOutAnimation(final double min, final double max, final int duration) {
        super(min, max, duration);
    }

    @Override
    public double determineValue() {
        final double x = ((double) this.ticks) / this.duration;
        return Math.pow(x, 4);
    }

}

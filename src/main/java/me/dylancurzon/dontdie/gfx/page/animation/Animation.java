package me.dylancurzon.dontdie.gfx.page.animation;

import me.dylancurzon.dontdie.Tickable;

public abstract class Animation implements Tickable {

    protected final double min;
    protected final double max;
    protected final int duration;

    protected int ticks;
    protected boolean completed;

    /**
     * @param min The minimum value that this Animation should produce. When ticks=0, this value
     * should be returned. Therefore, it can be thought of as the initial value.
     * @param max The maximum value that this Animation should produce. When ticks=duration, this
     * value should be returned. Therefore, it can be thought of as the final value.
     * @param duration The duration, in ticks, that this Animation should last for. When the
     * Animation has "finished" (ticks=duration), the final value is returned and further updates
     * have no effect.
     */
    public Animation(final double min, final double max, final int duration) {
        this.min = min;
        this.max = max;
        this.duration = duration;
    }

    /**
     * @return The current value calculated on demand. This method is effectively a pure function
     * taking only {@link this#ticks} as a parameter.
     */
    public abstract double determineValue();

    /**
     * Default way to deal with ticking for an {@link Animation}.
     */
    @Override
    public void tick() {
        if (this.completed) return;
        if (this.ticks++ >= this.duration) {
            this.completed = true;
        }
    }

    /**
     * @return true if {@link this#ticks} {@code =} {@link this#duration}.
     */
    public boolean isCompleted() {
        return this.completed;
    }

}

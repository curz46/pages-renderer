package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.GraphImmutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GraphMutableElement extends MutableElement {

    private final GraphImmutableElement element;
    private final List<Double> values = new ArrayList<>();

    private int ticks;
    private int resolutionX = 1;

    public GraphMutableElement(final Spacing margin, final InteractOptions interactOptions,
                               final GraphImmutableElement element) {
        super(margin, interactOptions);
        this.element = element;
    }

    public void setResolution(final int value) {
        this.resolutionX = value;
    }

    @Override
    public Vector2i calculateSize() {
        return this.element.getSize();
    }

    @Override
    public void tick() {
        final double value = this.element.getValueSupplier().get();
        this.values.add(value);
        final Consumer<MutableElement> consumer = this.element.getTickConsumer();
        if (consumer != null) {
            consumer.accept(this);
        }
        this.ticks++;
    }

    @Override
    public int[] getInteractMask() {
        final int[] mask = new int[this.getSize().getX() * this.getSize().getY()];
        for (int i = 0; i < mask.length; i++) {
            mask[i] = 1;
        }
        return mask;
    }

    @Override
    public void render(final PixelContainer container) {
        for (int dt = 0; dt < container.getWidth(); dt++) {
            final int index = (int) Math.floor(this.values.size() - (dt * this.resolutionX)) -
                (this.ticks % this.resolutionX);
            for (int y = 0; y < container.getHeight(); y++) {
                if ((container.getWidth() - 1 - dt) == 0 || y == (container.getHeight() - 1)) {
                    container.setPixel(container.getWidth() - dt, y, 0xFFBBBBBB);
                } else if (index % 5 == 0 && y % 5 == 0) {
                    container.setPixel(container.getWidth() - dt, y, 0xFFAAAAAA);
                }
            }
            if (index < 0 || index >= this.values.size()) continue;
            final double value = this.values.get(index);
            final int y = container.getHeight() - (int) Math.floor(value * container.getHeight());
            container.setPixel(container.getWidth() - dt, y, 0xFFFFFFFF);
        }
    }
}

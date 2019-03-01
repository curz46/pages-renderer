package me.dylancurzon.dontdie.gfx.page.elements;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.GraphMutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GraphImmutableElement extends ImmutableElement {

    private final Vector2i size;
    private final Supplier<Double> valueSupplier;
    private int ticks = 0;

    private GraphImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                  final Vector2i size, final Supplier<Double> valueSupplier,
                                  final Function<MutableElement, WrappingMutableElement> mutator,
                                  final InteractOptions interactOptions) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.size = size;
        this.valueSupplier = valueSupplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Supplier<Double> getValueSupplier() {
        return this.valueSupplier;
    }

    public Vector2i getSize() {
        return this.size;
    }

    @Override
    public MutableElement asMutable() {
        final List<Double> values = new ArrayList<>();
        final int resolutionX = 1;
        return super.doMutate(new GraphMutableElement(super.margin, super.interactOptions, this));
    }

    public static class Builder extends ImmutableElement.Builder<GraphImmutableElement, Builder> {

        private Vector2i size = Vector2i.of(50, 50);
        private Supplier<Double> valueFunction;

        @NotNull
        public Builder setSize(final Vector2i size) {
            this.size = size;
            return this;
        }

        @NotNull
        public Builder setSupplier(final Supplier<Double> valueFunction) {
            this.valueFunction = valueFunction;
            return this;
        }

        @Override
        @NotNull
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public GraphImmutableElement build() {
            if (this.valueFunction == null) {
                throw new RuntimeException("GraphImmutableElement.Builder requires ValueSupplier!");
            }
            return new GraphImmutableElement(
                super.margin,
                super.tickConsumer,
                this.size,
                this.valueFunction,
                super.mutator,
                super.interactOptions
            );
        }

    }


}

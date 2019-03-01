package me.dylancurzon.dontdie.gfx.page.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public abstract class ImmutableElement {

    protected final Spacing margin;
    protected final Consumer<MutableElement> tickConsumer;
    protected final Function<MutableElement, WrappingMutableElement> mutator;
    protected final InteractOptions interactOptions;

    protected ImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                               final Function<MutableElement, WrappingMutableElement> mutator,
                               final InteractOptions interactOptions) {
        if (interactOptions == null) {
            this.interactOptions = InteractOptions.empty();
        } else {
            this.interactOptions = interactOptions;
        }
        if (margin == null) {
            this.margin = Spacing.ZERO;
        } else {
            this.margin = margin;
        }
        this.tickConsumer = tickConsumer;
        this.mutator = mutator;
    }

    public MutableElement doMutate(final MutableElement element) {
        if (this.mutator != null) {
            return this.mutator.apply(element);
        }
        return element;
    }

    @NotNull
    public abstract MutableElement asMutable();

    @NotNull
    public Spacing getMargin() {
        return this.margin;
    }

    public Consumer<MutableElement> getTickConsumer() {
        return this.tickConsumer;
    }

    @NotNull
    public InteractOptions getInteractOptions() {
        return this.interactOptions;
    }

    public static abstract class Builder<T extends ImmutableElement, B extends Builder> {

        protected Spacing margin;
        protected Consumer<MutableElement> tickConsumer;
        protected Function<MutableElement, WrappingMutableElement> mutator;
        protected InteractOptions interactOptions;

        @NotNull
        public B setMargin(final Spacing margin) {
            this.margin = margin;
            return this.self();
        }

        @NotNull
        public B tick(final Consumer<MutableElement> tickConsumer) {
            this.tickConsumer = tickConsumer;
            return this.self();
        }

        @NotNull
        public B mutate(final Function<MutableElement, WrappingMutableElement> mutator) {
            this.mutator = mutator;
            return this.self();
        }

        @NotNull
        public B setInteractOptions(final InteractOptions options) {
            this.interactOptions = options;
            return this.self();
        }

        @NotNull
        public abstract B self();

        @NotNull
        public abstract T build();

    }

}

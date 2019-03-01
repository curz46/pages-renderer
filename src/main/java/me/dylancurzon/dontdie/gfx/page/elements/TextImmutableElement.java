package me.dylancurzon.dontdie.gfx.page.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.TextMutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public class TextImmutableElement extends ImmutableElement {

    private final TextSprite sprite;

    protected TextImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                   final TextSprite sprite,
                                   final Function<MutableElement, WrappingMutableElement> mutator,
                                   final InteractOptions interactOptions) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.sprite = sprite;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NotNull
    public MutableElement asMutable() {
        return super.doMutate(new TextMutableElement(super.margin, this));
    }

    @NotNull
    public TextSprite getSprite() {
        return this.sprite;
    }

    public static class Builder extends ImmutableElement.Builder<TextImmutableElement, Builder> {

        private TextSprite sprite;

        @NotNull
        public Builder setText(final TextSprite sprite) {
            this.sprite = sprite;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public TextImmutableElement build() {
            return new TextImmutableElement(
                super.margin,
                super.tickConsumer,
                this.sprite,
                super.mutator,
                super.interactOptions
            );
        }

    }

}

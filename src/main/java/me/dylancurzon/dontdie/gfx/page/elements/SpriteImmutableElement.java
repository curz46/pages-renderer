package me.dylancurzon.dontdie.gfx.page.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.OldSprite;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public abstract class SpriteImmutableElement extends ImmutableElement {

    private final OldSprite sprite;
    private final Sprite animatedSprite;

    public SpriteImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                  final Function<MutableElement, WrappingMutableElement> mutator,
                                  final InteractOptions interactOptions, final OldSprite sprite,
                                  final Sprite animatedSprite) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.sprite = sprite;
        this.animatedSprite = animatedSprite;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final SpriteImmutableElement element) {
        return new Builder(element);
    }

    public OldSprite getSprite() {
        return this.sprite;
    }

    public Sprite getAnimatedSprite() {
        return this.animatedSprite;
    }

    public static class StaticSpriteImmutableElement extends SpriteImmutableElement {

        private final OldSprite sprite;

        public StaticSpriteImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                            final OldSprite sprite,
                                            final Function<MutableElement, WrappingMutableElement> mutator,
                                            final InteractOptions interactOptions) {
            super(margin, tickConsumer, mutator, interactOptions, sprite, null);
            this.sprite = sprite;
        }

        @Override
        public MutableElement asMutable() {
            return super.doMutate(new MutableElement(super.margin, super.interactOptions) {
                @Override
                public Vector2i calculateSize() {
                    final OldSprite sprite = StaticSpriteImmutableElement.this.sprite;
                    return Vector2i.of(
                        sprite.getWidth(),
                        sprite.getHeight()
                    );
                }

                @Override
                public void tick() {
                    final Consumer<MutableElement> consumer = StaticSpriteImmutableElement.super.getTickConsumer();
                    if (consumer != null) {
                        consumer.accept(this);
                    }
                }
            });
        }

    }

    public static class AnimatedSpriteImmutableElement extends SpriteImmutableElement {

        private final Sprite sprite;

        public AnimatedSpriteImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                              final Sprite sprite,
                                              final Function<MutableElement, WrappingMutableElement> mutator,
                                              final InteractOptions interactOptions) {
            super(margin, tickConsumer, mutator, interactOptions, null, sprite);
            this.sprite = sprite;
        }

        @Override
        public MutableElement asMutable() {
            final Sprite.TickableSprite tickableSprite = this.sprite.createTickableSprite();
            return super.doMutate(new MutableElement(super.margin, super.interactOptions) {
                @Override
                public void tick() {
                    final Consumer<MutableElement> consumer = AnimatedSpriteImmutableElement.super.getTickConsumer();
                    if (consumer != null) {
                        consumer.accept(this);
                    }
                    tickableSprite.tick();
                }

                @Override
                public Vector2i calculateSize() {
                    return Vector2i.of(
                        AnimatedSpriteImmutableElement.this.sprite.getWidth(),
                        AnimatedSpriteImmutableElement.this.sprite.getHeight()
                    );
                }
            });
        }

    }

    public static class Builder extends ImmutableElement.Builder<SpriteImmutableElement, Builder> {

        protected OldSprite sprite;
        protected Sprite animatedSprite;

        protected Builder() {}

        protected Builder(final SpriteImmutableElement element) {
            this.interactOptions = element.interactOptions;
            this.animatedSprite = element.animatedSprite;
            this.tickConsumer = element.tickConsumer;
            this.mutator = element.mutator;
            this.sprite = element.sprite;
            this.margin = element.margin;
        }

        @NotNull
        public Builder setSprite(final OldSprite sprite) {
            if (this.animatedSprite != null) {
                throw new RuntimeException(
                    "SpriteImmutableElement.Builder: invalid usage, Sprite and AnimatedSprite "
                    + "should not both be set."
                );
            }
            this.sprite = sprite;
            return this;
        }

        @NotNull
        public Builder setAnimatedSprite(final Sprite animatedSprite) {
            if (this.sprite != null) {
                throw new RuntimeException(
                    "SpriteImmutableElement.Builder: invalid usage, Sprite and AnimatedSprite "
                        + "should not both be set."
                );
            }
            this.animatedSprite = animatedSprite;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public SpriteImmutableElement build() {
            if (this.animatedSprite != null) {
                return new AnimatedSpriteImmutableElement(
                    super.margin,
                    super.tickConsumer,
                    this.animatedSprite,
                    super.mutator,
                    super.interactOptions
                );
            }
            if (this.sprite == null) {
                throw new RuntimeException(
                    "SpriteImmutableElement.Builder requires a Sprite or AnimatedSprite to build!"
                );
            }
            return new StaticSpriteImmutableElement(
                super.margin,
                super.tickConsumer,
                this.sprite,
                super.mutator,
                super.interactOptions
            );
        }

    }

}

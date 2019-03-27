package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.util.Cached;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.function.Consumer;

public abstract class MutableElement {

    @NotNull
    protected final Spacing margin;
    @NotNull
    protected final InteractOptions interactOptions;

    protected MutableContainer parent;

//    private final Map<Key, Object> state = new HashMap<>();

    private final Cached<Vector2i> cachedSize = new Cached<>();

    protected MutableElement(@NotNull final Spacing margin, final InteractOptions interactOptions) {
        this.margin = margin;
        this.interactOptions = interactOptions;
    }

//    public <T> Optional<T> get(final Key<T> key) {
//        try {
//            if (!this.state.containsKey(key)) return Optional.empty();
//            //noinspection unchecked
//            return Optional.ofNullable((T) this.state.get(key));
//        } catch (final ClassCastException e) {
//            throw new RuntimeException(
//                "I have no idea how this happened, but I was unable to cast to the Key type."
//            );
//        }
//    }
//
//    public <T> void set(final Key<T> key, final T value) {
//        this.state.put(key, value);
//    }

    @NotNull
    public Spacing getMargin() {
        return this.margin;
    }

    @NotNull
    public InteractOptions getInteractOptions() {
        return this.interactOptions;
    }

    @NotNull
    public Vector2i getMarginedSize() {
        return this.getSize().add(
            Vector2i.of(
                this.margin.getLeft() + this.margin.getRight(),
                this.margin.getBottom() + this.margin.getTop()
            )
        );
    }

    public void setParent(final MutableContainer parent) {
        this.parent = parent;
    }

    public MutableContainer getParent() {
        return this.parent;
    }

    /**
     * Clicks on this element.
     * Note: if this {@link MutableElement} is a {@link MutableContainer}, it will also propagate the click event
     * through the hierarchy, such that any MutableElements it is responsible for rendering are able to handle it
     * themselves.
     * @param position A position relative to this MutableElement, such that the top-left corner of this element's
     *                 rendering bounds are (0, 0).
     */
    public void click(@NotNull final Vector2i position) {
        if (position.getX() < 0 || position.getX() >= this.getSize().getX() ||
            position.getY() < 0 || position.getY() >= this.getSize().getY()) {
            return;
        }
        final Consumer<MutableElement> consumer = this.interactOptions.getClickConsumer();
        if (consumer == null) {
            return;
        }
        // fire click
        consumer.accept(this);
    }

    public Vector2i getMousePosition(final MutableElement element) {
        return this.parent.getMousePosition(element);
    }

    public Vector2i getMousePosition() {
        if (this.parent == null) return null;
        return this.parent.getMousePosition(this);
    }

    public void tick() {}

    public Vector2i getSize() {
        return this.cachedSize.get()
            .orElseGet(() -> {
                final Vector2i size = this.calculateSize();
                this.cachedSize.set(size);
                return size;
            });
    }

    @NotNull
    public abstract Vector2i calculateSize();

}

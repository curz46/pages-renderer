package me.dylancurzon.openglrenderer.gfx.page;

import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FlattenedElement {

    private final Vector2i position;
    private final MutableElement mutableElement;
    @Nullable
    private FlattenedElement parentElement;

    public FlattenedElement(Vector2i position,
                            MutableElement mutableElement,
                            @Nullable FlattenedElement parentElement) {
        this.position = position;
        this.mutableElement = mutableElement;
        this.parentElement = parentElement;
    }

    public void setParentElement(@Nullable FlattenedElement parentElement) {
        this.parentElement = parentElement;
    }

    public Optional<Vector2i> getBoundA() {
        return Optional.ofNullable(parentElement)
            .map(FlattenedElement::getPosition);
    }

    public Optional<Vector2i> getBoundB() {
        return Optional.ofNullable(parentElement)
            .map(element -> element.getPosition().add(element.getMutableElement().getSize()));
    }

    public Vector2i getPosition() {
        return position;
    }

    public MutableElement getMutableElement() {
        return mutableElement;
    }

    public FlattenedElement getParentElement() {
        return parentElement;
    }
}

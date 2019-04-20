package me.dylancurzon.dontdie.gfx.window;

import me.dylancurzon.pages.event.MouseScrollEvent;
import me.dylancurzon.pages.event.bus.EventBus;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;

import java.util.function.Consumer;

/**
 * A window representation which exposes a number of methods which relate to the state of the window. This class also
 * acts as a {@link EventBus}, and any implementing classes can be expected to provide the following as observable
 * events...
 * - {@link me.dylancurzon.pages.event.MouseScrollEvent}
 * - {@link WindowMouseActionEvent}
 * - {@link WindowFocusEvent}
 * - {@link WindowKeyEvent}
 * In order to listen to these events, {@link this#subscribe(Class, Consumer)} can be called and the Window will notify
 * its listeners of the event whenever it occurs.
 */
public interface Window extends EventBus {

    /**
     * @return The id of this window.
     */
    long getId();

    /**
     * @return The title of this window.
     */
    String getTitle();

    /**
     * @return A {@link Vector2i} representing the pixel dimensions of this window.
     */
    Vector2i getDimensions();

    /**
     * @return The current mouse position given as a pixel coordinate, relative to the top-right corner of the window.
     */
    Vector2d getMousePosition();

    /**
     * @return {@code true} if the window is currently focused, {@code false} otherwise.
     */
    boolean isFocused();

    /**
     * @param code The key code to test
     * @return {@code true} If the given key code is currently pressed down, {@code false} otherwise.
     */
    boolean isKeyPressed(int code);

    /**
     * @param button The {@link MouseButton} to test
     * @return {@code true} If the given {@link MouseButton} is currently pressed down, {@code} false otherwise.
     */
    boolean isMousePressed(MouseButton button);

    default void doOnMouseScroll(Consumer<MouseScrollEvent> consumer) {
        subscribe(MouseScrollEvent.class, consumer);
    }

    default void doOnMouseAction(Consumer<WindowMouseActionEvent> consumer) {
        subscribe(WindowMouseActionEvent.class, consumer);
    }

    default void doOnMousePress(Consumer<WindowMouseActionEvent.Press> consumer) {
        subscribe(WindowMouseActionEvent.Press.class, consumer);
    }

    default void doOnMouseRelease(Consumer<WindowMouseActionEvent.Release> consumer) {
        subscribe(WindowMouseActionEvent.Release.class, consumer);
    }

    default void doOnFocusChange(Consumer<WindowFocusEvent> consumer) {
        subscribe(WindowFocusEvent.class, consumer);
    }

    default void doOnFocusStart(Consumer<WindowFocusEvent.Start>consumer) {
        subscribe(WindowFocusEvent.Start.class, consumer);
    }

    default void doOnFocusEnd(Consumer<WindowFocusEvent.End> consumer) {
        subscribe(WindowFocusEvent.End.class, consumer);
    }

    default void doOnKeyAction(Consumer<WindowKeyEvent> consumer) {
        subscribe(WindowKeyEvent.class, consumer);
    }

    default void doOnKeyAction(int code, Consumer<WindowKeyEvent> consumer) {
        doOnKeyAction(event -> {
            if (event.getKeyCode() == code) consumer.accept(event);
        });
    }

    default void doOnKeyPress(Consumer<WindowKeyEvent.Press> consumer) {
        subscribe(WindowKeyEvent.Press.class, consumer);
    }

    default void doOnKeyPress(int code, Consumer<WindowKeyEvent.Press> consumer) {
        doOnKeyPress(event -> {
            if (event.getKeyCode() == code) consumer.accept(event);
        });
    }

    default void doOnKeyRelease(Consumer<WindowKeyEvent.Release> consumer) {
        subscribe(WindowKeyEvent.Release.class, consumer);
    }

    default void doOnKeyRelease(int code, Consumer<WindowKeyEvent.Release> consumer) {
        doOnKeyRelease(event -> {
            if (event.getKeyCode() == code) consumer.accept(event);
        });
    }

    default void doOnKeyRepeat(Consumer<WindowKeyEvent.Repeat> consumer) {
        subscribe(WindowKeyEvent.Repeat.class, consumer);
    }

    default void doOnKeyRepeat(int code, Consumer<WindowKeyEvent.Repeat> consumer) {
        doOnKeyRepeat(event -> {
            if (event.getKeyCode() == code) consumer.accept(event);
        });
    }

}

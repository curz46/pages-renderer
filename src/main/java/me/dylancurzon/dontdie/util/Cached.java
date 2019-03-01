package me.dylancurzon.dontdie.util;

import java.util.Optional;

public class Cached<T> {

    private T object;

    public Cached() {}

    public Cached(final T object) {
        this.object = object;
    }

    public Optional<T> get() {
        return Optional.ofNullable(this.object);
    }

    public void set(final T object) {
        this.object = object;
    }

    public void clear() {
        this.object = null;
    }

}

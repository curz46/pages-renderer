package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.util.Vector2d;

public interface Camera {

    void setPosition(final Vector2d position);

    void setAspectRatio(final double ratio);

    void setZoom(final double zoom);

    Vector2d getPosition();

    double getAspectRatio();

    double getZoom();

}

package me.dylancurzon.testgame.designer.tool;

import me.dylancurzon.openglrenderer.Tickable;
import me.dylancurzon.openglrenderer.gfx.Renderer;
import me.dylancurzon.pages.util.Vector2i;

public abstract class Tool extends Renderer implements Tickable {

    public abstract void click(Vector2i position);

}

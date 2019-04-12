package me.dylancurzon.dontdie.designer.tool;

import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.pages.util.Vector2i;

public abstract class Tool extends Renderer implements Tickable {

    public abstract void click(Vector2i position);

}

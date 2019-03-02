package me.dylancurzon.dontdie.gfx.page;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.dontdie.gfx.page.elements.SpriteImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

public interface Elements {

//    SpriteImmutableElement ARROW_UP = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(0, 0, 16))
//        .build();
//    SpriteImmutableElement ARROW_DOWN = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(1, 0, 16))
//        .build();
//    SpriteImmutableElement START = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(2, 0, 16))
//        .build();
//    SpriteImmutableElement STOP = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(3, 0, 16))
//        .build();
//    // TODO: CHECKBOX sprites need to be merged into one SpriteImmutableElement with a stateful MutableElement
//    // this could be done by adding a WrappingMutableElement class which takes a MutableElement instance, generated
//    // by the Static/AnimatedSpriteImmutableElement#asMutable method, and then overrides as required by creating a
//    // subclass of the WrappedMutableElement. Could have generics so that super.<field name> calls access the correct
//    // subclass of MutableElement.
//    // potential names:
//    SpriteImmutableElement CHECKBOX_UNCHECKED = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(0, 1, 16))
//        .build();
//    SpriteImmutableElement CHECKBOX_CHECKED = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(1, 1, 16))
//        .mutate(element -> new WrappingMutableElement(element) {
//            // Manual interact-able area for checkboxes, since they are transparent in the centre
//            // but still have a square-based clickable region
//            // TODO: should look into automating this, for example:
//            // SpriteSheet.GUI_INTERACT.getSprite(x, y, 16).getPixels();
//            @Override
//            @NotNull
//            public int[] getInteractMask() {
//                final Vector2i size = super.getSize();
//                final int[] mask = new int[size.getX() * size.getY()];
//                for (int x = 2; x < 14; x++) {
//                    for (int y = 2; y < 14; y++) {
//                        mask[x + y * size.getX()] = 1;
//                    }
//                }
//                return mask;
//            }
//        })
//        .build();
//    //
//    SpriteImmutableElement CIRCLE = SpriteImmutableElement.builder()
//        .setSprite(SpriteSheet.GUI_SHEET.getSprite(2, 1, 16))
//        .build();
//    SpriteImmutableElement LARGE_BUTTON = SpriteImmutableElement.builder()
//        .setSprite(StaticSprite.loadSprite("menu-button-small.png"))
//        .build();

}

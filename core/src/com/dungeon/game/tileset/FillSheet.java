package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.TileSheet;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.object.powerups.PowerupsSheet;

public class FillSheet extends TileSheet {

    public final TextureRegion FILL_1 = getTile(0, 0, 1, 1);
    public static final String FILL = "fill";
    private final Animation<TextureRegion> FILL_ANIMATION = loop(Float.MAX_VALUE, FILL_1);

    public FillSheet() {
        super(ResourceManager.instance().getTexture("fill.png"), 1);
    }

    public static Animation<TextureRegion> fill() {
        return new FillSheet().FILL_ANIMATION;
    }
}

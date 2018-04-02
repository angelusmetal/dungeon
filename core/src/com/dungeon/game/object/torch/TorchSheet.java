package com.dungeon.game.object.torch;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;
import com.dungeon.engine.resource.ResourceManager;

public class TorchSheet extends Tileset {

	private final TextureRegion TORCH_1 = getTile(0, 0);
	private final TextureRegion TORCH_2 = getTile(1, 0);
	private final TextureRegion TORCH_3 = getTile(2, 0);
	private final TextureRegion TORCH_4 = getTile(3, 0);
	private final TextureRegion TORCH_5 = getTile(0, 1);
	private final TextureRegion TORCH_6 = getTile(1, 1);
	private final TextureRegion TORCH_7 = getTile(2, 1);
	private final TextureRegion TORCH_8 = getTile(3, 1);
	private final Animation<TextureRegion> TORCH_ANIMATION = loop(0.05f, TORCH_1, TORCH_2, TORCH_3, TORCH_4, TORCH_5, TORCH_6, TORCH_7, TORCH_8);

	public static final String IDLE = "torch_idle";

	TorchSheet() {
		super(ResourceManager.instance().getTexture("torch.png"), 32);
	}

	public static Animation<TextureRegion> idle() {
		return new TorchSheet().TORCH_ANIMATION;
	}

}

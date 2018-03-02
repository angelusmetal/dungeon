package com.dungeon.game.tileset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Tileset;

public class TorchTileset extends Tileset {

	private final TextureRegion TORCH_1 = getTile(0, 0);
	private final TextureRegion TORCH_2 = getTile(1, 0);
	private final TextureRegion TORCH_3 = getTile(2, 0);
	private final TextureRegion TORCH_4 = getTile(3, 0);
	private final TextureRegion TORCH_5 = getTile(0, 1);
	private final TextureRegion TORCH_6 = getTile(1, 1);
	private final TextureRegion TORCH_7 = getTile(2, 1);
	private final TextureRegion TORCH_8 = getTile(3, 1);
	public final Animation<TextureRegion> TORCH_ANIMATION = new Animation<>(0.05f, TORCH_1, TORCH_2, TORCH_3, TORCH_4, TORCH_5, TORCH_6, TORCH_7, TORCH_8);

	TorchTileset() {
		super(new Texture("torch.png"), 32);
		TORCH_ANIMATION.setPlayMode(Animation.PlayMode.LOOP);
	}

}

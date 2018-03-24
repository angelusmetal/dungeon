package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.SlimeAcidTileset;

public class AcidSlimeFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> dieAnimation;
	final Animation<TextureRegion> poolFloodAnimation;
	final Animation<TextureRegion> poolDryAnimation;
	final Animation<TextureRegion> blobAnimation;
	final Animation<TextureRegion> splatAnimation;

	final Light characterLight;
	final Light poolLight;

	final DrawContext drawContext;

	public AcidSlimeFactory(GameState state) {
		this.state = state;
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.IDLE, SlimeAcidTileset::idle);
		attackAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.ATTACK, SlimeAcidTileset::attack);
		dieAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.DIE, SlimeAcidTileset::die);
		// Pool animations
		poolFloodAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.POOL_FLOOD, SlimeAcidTileset::poolFlood);
		poolDryAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.POOL_DRY, SlimeAcidTileset::poolDry);
		// Blob animations
		blobAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.BLOB, SlimeAcidTileset::blob);
		splatAnimation = ResourceManager.instance().getAnimation(SlimeAcidTileset.SPLAT, SlimeAcidTileset::splat);

		characterLight = new Light(100, new Color(0, 1, 0, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateMedium);
		poolLight = new Light(100, new Color(0, 0.5f, 0, 0.2f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		// Draw context
		drawContext = new ColorContext(new Color(1, 1, 1, 0.5f));
	}

	@Override
	public Entity build(Vector2 origin) {
		return new AcidSlime(this, origin);
	}
}

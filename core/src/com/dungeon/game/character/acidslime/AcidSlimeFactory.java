package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class AcidSlimeFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> dieAnimation;
	final Animation<TextureRegion> poolFloodAnimation;
	final Animation<TextureRegion> poolDryAnimation;
	final Animation<TextureRegion> blobAnimation;
	final Animation<TextureRegion> splatAnimation;

	final Particle.Builder blob;
	final Particle.Builder splat;

	final Light characterLight;
	final Light poolLight;
	final Light blobLight;

	final DrawContext drawContext;

	public AcidSlimeFactory(GameState state) {
		this.state = state;
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.IDLE, AcidSlimeSheet::idle);
		attackAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.ATTACK, AcidSlimeSheet::attack);
		dieAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.DIE, AcidSlimeSheet::die);
		// Pool animations
		poolFloodAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.POOL_FLOOD, AcidSlimeSheet::poolFlood);
		poolDryAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.POOL_DRY, AcidSlimeSheet::poolDry);
		// Blob animations
		blobAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.BLOB, AcidSlimeSheet::blob);
		splatAnimation = ResourceManager.instance().getAnimation(AcidSlimeSheet.SPLAT, AcidSlimeSheet::splat);

		characterLight = new Light(100, new Color(0, 1, 0, 0.5f), Light.RAYS_TEXTURE, () -> 1f, Light::rotateMedium);
		poolLight = new Light(100, new Color(0, 0.5f, 0, 0.2f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);
		blobLight = new Light(30, new Color(0, 0.5f, 0, 0.2f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		blob = new Particle.Builder()
				.speed(50)
				.color(new Color(1, 1, 1, 0.5f))
				.mutate(Particle.zAccel(-200))
				.timeToLive(10);
		splat = new Particle.Builder()
				.timeToLive(splatAnimation.getAnimationDuration());

		// Draw context
		drawContext = new ColorContext(new Color(1, 1, 1, 0.5f));
	}

	@Override
	public Entity build(Vector2 origin) {
		return new AcidSlime(this, origin);
	}
}

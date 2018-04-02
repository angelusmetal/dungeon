package com.dungeon.game.character.fireslime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.Particle;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.render.ColorContext;
import com.dungeon.engine.render.DrawContext;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.character.slime.Slime;
import com.dungeon.game.character.slime.SlimeSheet;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class FireSlimeFactory implements EntityFactory.EntityTypeFactory {

	final GameState state;

	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> projectileAnimation;
	final Animation<TextureRegion> explosionAnimation;

	final Projectile.Builder bullet;
	final Particle.Builder bulletExplosion;

	final Light characterLight;
	final Light bulletLight;

	final DrawContext drawContext;

	public FireSlimeFactory(GameState state) {
		this.state = state;
		// Character animations
		idleAnimation = ResourceManager.instance().getAnimation(FireSlimeSheet.IDLE, FireSlimeSheet::idle);
		projectileAnimation = ResourceManager.instance().getAnimation(FireSlimeSheet.PROJECTILE, FireSlimeSheet::projectile);
		explosionAnimation = ResourceManager.instance().getAnimation(FireSlimeSheet.EXPLOSION, FireSlimeSheet::explosion);

		characterLight = new Light(100, new Color(1, 0.5f, 0, 0.8f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		bulletLight = new Light(50, new Color(1, 0.5f, 0, 0.5f), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);

		bullet = new Projectile.Builder()
				.speed(100)
				.timeToLive(10)
				.targetPredicate(PlayerCharacter.IS_PLAYER)
				.damage(30);
		bulletExplosion = new Particle.Builder()
				.timeToLive(explosionAnimation.getAnimationDuration());

		// Draw context
		drawContext = new ColorContext(new Color(1, 1, 1, 0.8f));
	}

	@Override
	public Entity build(Vector2 origin) {
		return new FireSlime(this, origin);
	}
}

package com.dungeon.game.character.assassin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Projectile;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.DrawFunction;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.ConfigUtil;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.moandjiezana.toml.Toml;

public class AssassinFactory implements EntityFactory.EntityTypeFactory {

	public static final String ASSASSIN_WALK = "assassin_walk";
	public static final String ASSASSIN_ATTACK = "assassin_attack";
	public static final String ASSASSIN_IDLE = "assassin_idle";

	public static final String HIT_ANIMATION = "invisible";
	public static final String SLASH_ANIMATION = "melee_slash";

	final Animation<TextureRegion> attackAnimation;
	final Animation<TextureRegion> idleAnimation;
	final Animation<TextureRegion> walkAnimation;
	final Animation<TextureRegion> hitAnimation;
	final Animation<TextureRegion> slashAnimation;

	final EntityPrototype character;
	private final EntityPrototype hit;
	private final EntityPrototype slash;

	public AssassinFactory() {
		Toml config = ConfigUtil.getTomlMap(GameState.getConfiguration(), "creatures", "id").get("ASSASSIN");
		int health = config.getLong("health", 150L).intValue();
		float speed = config.getLong("speed", 60L).floatValue();
		float friction = config.getLong("friction", 10L).floatValue();
		float hitDamage = config.getLong("bulletDamage", 35L).floatValue();

		attackAnimation = ResourceManager.getAnimation(ASSASSIN_ATTACK);
		idleAnimation = ResourceManager.getAnimation(ASSASSIN_IDLE);
		walkAnimation = ResourceManager.getAnimation(ASSASSIN_WALK);
		hitAnimation = ResourceManager.getAnimation(HIT_ANIMATION);
		slashAnimation = ResourceManager.getAnimation(SLASH_ANIMATION);

		Vector2 characterBoundingBox = new Vector2(14, 22);
		Vector2 characterDrawOffset = new Vector2(16, 13);

		Vector2 hitBoundingBox = new Vector2(20, 20);
		Vector2 hitDrawOffset = new Vector2(16, 16);

		Light bulletLight = new Light(60, new Color(0.8f, 0.3f, 0.2f, 0.5f), Light.NORMAL_TEXTURE, () -> 1f, Light::noRotate);

		character = new EntityPrototype()
				.boundingBox(characterBoundingBox)
				.drawOffset(characterDrawOffset)
				.health(health)
				.speed(speed)
				.friction(friction);
		hit = new EntityPrototype()
				.animation(hitAnimation)
				.boundingBox(hitBoundingBox)
				.drawOffset(hitDrawOffset)
				.light(bulletLight)
				.speed(0)
				.timeToLive(slashAnimation.getAnimationDuration())
				.hitPredicate(PlayerEntity.HIT_NON_PLAYERS)
				.damage(hitDamage);
		slash = new EntityPrototype()
				.animation(slashAnimation)
				.boundingBox(hitBoundingBox)
				.drawOffset(hitDrawOffset)
				.light(bulletLight)
				.timeToLive(slashAnimation.getAnimationDuration());
	}


	public Assassin build(Vector2 origin) {
		return new Assassin(origin, this);
	}

	/**
	 * The hit is a short lived projectile with no speed. It is invisible and lives as long as the slash
	 * @param origin
	 * @return
	 */
	Entity createHit(Vector2 origin, Vector2 aim) {
		GameState.addEntity(createSlash(origin, aim));
		return new Projectile(origin, hit) {
			@Override protected void onTileCollision(boolean horizontal) {}
		};
	}

	private Entity createSlash(Vector2 origin, Vector2 aim) {
		Entity entity = new Entity(origin, slash);
		entity.setDrawFunction(DrawFunction.rotateVector(aim).get());
		return entity;
	}
}

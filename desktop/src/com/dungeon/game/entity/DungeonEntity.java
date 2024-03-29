package com.dungeon.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;

import java.util.EnumMap;

import static com.dungeon.game.Game.text;

public class DungeonEntity extends Entity implements Drawable, Movable {

	public static ShaderProgram shader = Resources.shaders.get("df_vertex.glsl|solid_color_fragment.glsl");

	// Hackish way to do control the solid shader duration
	protected static final Color[] HIGHLIGHT_COLORS = new Color[] {new Color(0xb222228f), new Color(0xffffff8f), new Color(0xb222228f)};
	protected float highlightDuration = 0.15f;
	protected float highlightUntil = 0f;

	public enum SpeedAffix { CHILL, FREEZE, HASTE }
	protected EnumMap<SpeedAffix, Float> speedMultipliers = new EnumMap<>(SpeedAffix.class);
	protected float currentSpeed;

	/**
	 * Create an entity at origin, from the specified prototype
	 * @param prototype Prototype to build entity from
	 * @param origin Origin to build entity at
	 */
	public DungeonEntity(EntityPrototype prototype, Vector2 origin) {
		super(prototype, origin);
		updateCurrentSpeed();
	}

	/**
	 * Copy constructor. Creates a copy of the provided entity at the same origin
	 * @param other Original entity to copy from
	 */
	public DungeonEntity(Entity other) {
		super(other);
		updateCurrentSpeed();
	}

	@Override
	public void draw(SpriteBatch batch) {
		if (highlightUntil > Engine.time()) {
			batch.end();
			shader.bind();
			float colorDuration = highlightDuration / HIGHLIGHT_COLORS.length;
			Color highlight = HIGHLIGHT_COLORS[(int) ((Engine.time() - (highlightUntil - highlightDuration)) / colorDuration)];
			shader.setUniformf("u_color", highlight);
			ShaderProgram otherShader = batch.getShader();
			batch.setShader(shader);
			batch.begin();
			super.draw(batch);
			batch.end();
			batch.setShader(otherShader);
			batch.begin();
		} else {
			super.draw(batch);
		}
	}

	@Override
	public Material getFrame() {
		return animation.getKeyFrame((Engine.time() - animationStart) * (currentSpeed / super.getSpeed()));
	}

	public void hit(Attack attack) {
		onHit();
		onHitTraits.forEach(m -> m.accept(this));
		if (canBeHurt() && highlightUntil < Engine.time()) {
			health -= attack.getDamage();
			highlightUntil = Engine.time() + highlightDuration;
			if (attack.getDamage() > 1) {
				Engine.overlayTexts.add(text(getOrigin(), "" + (int) attack.getDamage(), new Color(1, 0.5f, 0.2f, 0.5f)).fadeout(1).move(0, 20));
			}
			if (health <= 0) {
				expire();
			} else if (attack.getKnockback() > 0) {
				Vector2 knockback = getOrigin().cpy().sub(attack.getEmitter().getOrigin()).setLength(attack.getKnockback() * this.knockback);
				impulse(knockback);
			}
		}
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof DungeonEntity) {
			return onEntityCollision((DungeonEntity) entity);
		} else {
			return false;
		}
	}

	protected boolean onEntityCollision(DungeonEntity entity) {
		return false;
	}

	/** Returns true if this entity is within any player's viewport */
	public boolean inPlayerViewport() {
		for (Player p : Players.all()) {
			if (p.getViewPort().isInViewPort(this)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public float getSpeed() {
		return currentSpeed;
	}

	@Override
	public void setSpeed(float speed) {
		super.setSpeed(speed);
		updateCurrentSpeed();
	}

	/**
	 * Add a speed multiplier for the specified affix ONLY if the affix is not already present.
	 * In other words, speed multipliers for the same affixes do not stack.
	 */
	public void addSpeedMultiplier(SpeedAffix affix, float multiplier) {
		if (!speedMultipliers.containsKey(affix)) {
			speedMultipliers.put(affix, multiplier);
		}
		updateCurrentSpeed();
	}

	/**
	 * Remove the speed multiplier associated with a specific affix (if present)
	 */
	public void removeSpeedMultiplier(SpeedAffix affix) {
		speedMultipliers.remove(affix);
		updateCurrentSpeed();
	}

	private void updateCurrentSpeed() {
		currentSpeed = super.getSpeed();
		for (float multiplier : speedMultipliers.values()) {
			currentSpeed *= multiplier;
		}
	}
}

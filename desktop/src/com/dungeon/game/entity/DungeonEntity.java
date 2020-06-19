package com.dungeon.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.TraitSupplier;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.player.Player;
import com.dungeon.game.player.Players;

import static com.dungeon.game.Game.text;

public class DungeonEntity extends Entity implements Drawable, Movable {

	private static ShaderProgram shader = Resources.shaders.get("df_vertex.glsl|solid_color_fragment.glsl");

	// Hackish way to do control the solid shader duration
	protected static final float HIGHLIGHT_DURATION = 0.15f;
	protected static final Color[] HIGHLIGHT_COLORS = new Color[] {new Color(0xb222228f), new Color(0xffffff8f), new Color(0xb222228f)};
	protected static final float COLOR_DURATION = HIGHLIGHT_DURATION / HIGHLIGHT_COLORS.length;
	protected float highlightUntil = 0f;

	/**
	 * Create an entity at origin, from the specified prototype
	 * @param prototype Prototype to build entity from
	 * @param origin Origin to build entity at
	 */
	public DungeonEntity(EntityPrototype prototype, Vector2 origin) {
		super(prototype, origin);
	}

	/**
	 * Copy constructor. Creates a copy of the provided entity at the same origin
	 * @param other Original entity to copy from
	 */
	public DungeonEntity(Entity other) {
		super(other);
	}

	@Override
	public void draw(SpriteBatch batch, ViewPort viewPort) {
		if (highlightUntil > Engine.time()) {
			batch.end();
			shader.begin();
			Color highlight = HIGHLIGHT_COLORS[(int) ((Engine.time() - (highlightUntil - HIGHLIGHT_DURATION)) / COLOR_DURATION)];
			shader.setUniformf("u_color", highlight);
			shader.end();
			ShaderProgram otherShader = batch.getShader();
			batch.setShader(shader);
			batch.begin();
			super.draw(batch, viewPort);
			batch.end();
			batch.setShader(otherShader);
			batch.begin();
		} else {
			super.draw(batch, viewPort);
		}
	}

	public void hit(Attack attack) {
		if (canBeHurt()) {
			health -= attack.getDamage();
			onHitTraits.forEach(m -> m.accept(this));
			highlightUntil = Engine.time() + HIGHLIGHT_DURATION;
			onHit();
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

}

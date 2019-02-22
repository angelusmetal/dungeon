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
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.resource.Resources;

import static com.dungeon.game.Game.text;

public class DungeonEntity extends Entity implements Drawable, Movable {

	private static ShaderProgram shader = Resources.shaders.get("df_vertex.glsl|solid_color_fragment.glsl");

	// Hackish way to do control the solid shader duration
	private float whiteUntil = 0f;

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
		if (whiteUntil > Engine.time()) {
			batch.end();
			shader.begin();
			shader.setUniformf("u_color", new Color(1f, 1f, 1f, 1f));
			shader.end();
			batch.setShader(shader);
			batch.begin();
			super.draw(batch, viewPort);
			batch.end();
			batch.setShader(null);
			batch.begin();
		} else {
			super.draw(batch, viewPort);
		}
	}

	public void hit(Attack attack) {
		if (canBeHurt()) {
			health -= attack.getDamage();
			onHitTraits.forEach(m -> m.accept(this));
			onHit();
			whiteUntil = Engine.time() + 0.1f;
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


	static public <T extends Entity> TraitSupplier<T> generateLoot() {
		return e -> entity -> {
			Entity loot = Game.build(Game.createLoot(), entity.getOrigin());
			loot.setZPos(15);
			// TODO Is this really ok?
			loot.getTraits().add(Traits.fadeIn(1f, 1f).get(loot));
			Engine.entities.add(loot);
		};
	}
}

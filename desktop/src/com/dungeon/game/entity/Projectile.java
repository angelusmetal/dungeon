package com.dungeon.game.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.combat.Attack;

import java.util.function.Function;

// TODO If this needs to exist, move this to game/toml files
/**
 * Base class for all projectiles
 */
public abstract class Projectile extends DungeonEntity implements Movable, Drawable {

	public static final Function<DungeonEntity, Boolean> NO_FRIENDLY_FIRE = entity -> !(entity instanceof PlayerEntity) && entity.isSolid();

	/** Damage to inflict upon hitting a target */
	protected Function<Entity, Attack> attackSupplier;

	public Projectile(Vector2 origin, EntityPrototype prototype, Function<Entity, Attack> attackSupplier) {
		super(prototype, origin);
		this.attackSupplier = attackSupplier;
	}

	@Override
	protected void onTileCollision(boolean horizontal) {
		if (bounciness == 0) {
			expire();
		}
	}

	@Override
	protected boolean onEntityCollision(DungeonEntity entity) {
		if (!expired && hitPredicate.test(entity) && entity.canBeHit()) {
			expire();
			entity.hit(attackSupplier.apply(this));
			return true;
		} else {
			return false;
		}
	}

}

package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.game.combat.Attack;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for all projectiles
 */
public abstract class Projectile extends Entity implements Movable, Drawable {

	public static final Function<Entity, Boolean> NO_FRIENDLY_FIRE = entity -> !(entity instanceof PlayerEntity) && entity.isSolid();

	/** Damage to inflict upon hitting a target */
	protected Supplier<Attack> attackSupplier;

	public Projectile(Vector2 origin, EntityPrototype prototype, Supplier<Attack> attackSupplier) {
		super(origin, prototype);
		this.attackSupplier = attackSupplier;
	}

	@Override
	protected void onTileCollision(boolean horizontal) {
		if (bounciness == 0) {
			expire();
		}
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (!expired && hitPredicate.test(entity) && entity.canBeHit()) {
			expire();
			Attack attack = attackSupplier.get();
			entity.hit(attack.getDamage());
			return true;
		} else {
			return false;
		}
	}

}

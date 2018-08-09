package com.dungeon.game.character.acidslime;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.game.combat.Attack;
import com.dungeon.game.combat.DamageType;
import com.dungeon.game.state.GameState;

public class AcidPool extends Entity {

	private final AcidSlimeFactory factory;

	public AcidPool(Vector2 origin, AcidSlimeFactory factory) {
		super(factory.pool, origin);
		this.factory = factory;
	}

	@Override
	public void think() {
		if (GameState.time() > expirationTime - 0.5f && getCurrentAnimation().getAnimation() != factory.poolDryAnimation) {
			setCurrentAnimation(factory.poolDryAnimation);
		}
	}

	@Override
	protected boolean onEntityCollision(Entity entity) {
		if (entity instanceof PlayerEntity) {
			Attack attack = new Attack(this, factory.damagePerSecond * GameState.frameTime(), DamageType.ELEMENTAL, 0);
			entity.hit(attack);
			return true;
		} else {
			return false;
		}
	}
}

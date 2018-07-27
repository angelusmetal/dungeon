package com.dungeon.game.object.props;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.state.GameState;

public class Chest extends Entity {
	private boolean canBeHit = true;
	public Chest(Vector2 origin, EntityPrototype prototype) {
		super(origin, prototype);
	}
	@Override public void onHit() {
		Rand.doBetween(1, 2, () ->
				GameState.addEntity(GameState.build(EntityType.WOOD_PARTICLE, getPos()))
		);
		if (health < 1) {
			health = 1;
			canBeHit = false;
			setCurrentAnimation(ResourceManager.getAnimation("chest_opening"));
			Entity loot = GameState.build(GameState.createLoot(), getPos());
			loot.setZPos(15);
			GameState.addEntity(loot);
		}
	}
	@Override public boolean isSolid() {
		return true;
	}
	@Override public boolean canBeHit() {
		return canBeHit;
	}
}

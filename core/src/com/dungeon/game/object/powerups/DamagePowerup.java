package com.dungeon.game.object.powerups;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class DamagePowerup extends Entity {

	public DamagePowerup(GameState state, Vector2 position) {
		super(new Body(position, new Vector2(10, 10)));
//		setCurrentAnimation(new GameAnimation(AnimationType.IDLE, ResourceManager.instance().getAnimation(ExitPlatformSheet.DAMAGE, ExitPlatformSheet::damage), state.getStateTime()));
	}

	@Override
	public boolean isExpired(float time) {
		return expired;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

}

package com.dungeon.game.object.powerups;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.physics.Body;
import com.dungeon.game.state.GameState;

public class DamagePowerup extends Entity {

	private static final Vector2 BOUNDING_BOX = new Vector2(10, 10);
	private static final Vector2 DRAW_OFFSET = new Vector2(10, 10);

	public DamagePowerup(GameState state, Vector2 position) {
		super(new Body(position, BOUNDING_BOX), DRAW_OFFSET);
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

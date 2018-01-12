package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.dungeon.game.GameState;
import com.dungeon.game.object.Tombstone;

public abstract class PlayerCharacter extends Character {

	private Vector2 controlDirection = Vector2.Zero;

	public PlayerCharacter(Vector2 pos) {
		super(pos);
	}

	public Vector2 getControlDirection() {
		return controlDirection;
	}

	public void setControlDirection(Vector2 controlDirection) {
		this.controlDirection = controlDirection;
	}

	@Override
	public void think(GameState state) {
		this.setLinearVelocity(controlDirection);
	}

	@Override
	protected void onExpire(GameState state) {
		Tombstone tombstone = new Tombstone(state, getPos());
		state.addEntity(tombstone);
	}
}

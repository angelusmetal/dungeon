package com.dungeon.engine.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dungeon.game.GameState;

import java.util.Arrays;

/**
 * Hacky class to model solid tiles (for collisions)
 */
public class SolidTile extends Entity<SolidTile.AnimationType> {

	public enum AnimationType {
		IDLE
	}

	public SolidTile(Vector2 startingPos) {
		super(startingPos);
	}

	@Override
	public void attachToPhysics(GameState state){
		// TODO Create objects for walls and move this code accordingly
		BodyDef blockDef = new BodyDef();
		blockDef.type = BodyDef.BodyType.StaticBody;

		int tile_width = state.getLevelTileset().tile_width;
		int tile_height = state.getLevelTileset().tile_height;
		int col = (int) startingPosition.x;
		int row = (int) startingPosition.y;
		PolygonShape shape = new PolygonShape();
		shape.set(new Vector2[]{
				new Vector2((col+1) * tile_width, (row+1) * tile_height),
				new Vector2(col * tile_width, (row+1) * tile_height),
				new Vector2(col * tile_width, row * tile_height),
				new Vector2((col+1) * tile_width, row * tile_height),
		});
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		body = state.getWorld().createBody(blockDef);
		body.createFixture(fixtureDef);

		shape.dispose();
		body.setUserData(this);
	}
	@Override
	public boolean isExpired(float time) {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

}

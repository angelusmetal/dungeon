package com.dungeon.game;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.Tileset;

/**
 * Provides convenience methods for conversion between tileset coordinates
 */
public class TilesetHelper {
	private final Tileset tileset;

	public TilesetHelper(Tileset tileset) {
		this.tileset = tileset;
	}

	public Vector2 tileOnPosition(Vector2 position) {
		return new Vector2((int)(position.x / tileset.tile_size), (int)(position.y / tileset.tile_size));
	}

	public float roundToTile(float val, boolean increasing) {
		if (increasing) {
			return val - val % tileset.tile_size - 1;
		} else {
			return val + tileset.tile_size - val % tileset.tile_size;
		}
	}
}

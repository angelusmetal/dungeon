package com.dungeon.game.level;

import com.dungeon.game.tileset.Tileset;

public class TilePrototype {
	private final Tileset floor;
	private final Tileset wall;
	private final boolean solid;

	private TilePrototype(Builder builder) {
		floor = builder.floor;
		wall = builder.wall;
		solid = builder.solid;
	}

	public Tileset getFloor() {
		return floor;
	}

	public Tileset getWall() {
		return wall;
	}

	public boolean isSolid() {
		return solid;
	}

	public static final class Builder {
		private Tileset floor;
		private Tileset wall;
		private boolean solid;

		public Builder() {
		}

		public Builder(TilePrototype copy) {
			this.floor = copy.getFloor();
			this.wall = copy.getWall();
			this.solid = copy.isSolid();
		}

		public Builder floor(Tileset floor) {
			this.floor = floor;
			return this;
		}

		public Builder wall(Tileset wall) {
			this.wall = wall;
			return this;
		}

		public Builder solid(boolean solid) {
			this.solid = solid;
			return this;
		}

		public TilePrototype build() {
			return new TilePrototype(this);
		}
	}
}

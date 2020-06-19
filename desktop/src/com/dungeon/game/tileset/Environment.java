package com.dungeon.game.tileset;

import java.util.List;

public class Environment {
	private final List<EnvironmentLevel> levels;

	public Environment(List<EnvironmentLevel> levels) {
		this.levels = levels;
	}

	public List<EnvironmentLevel> getLevels() {
		return levels;
	}
}

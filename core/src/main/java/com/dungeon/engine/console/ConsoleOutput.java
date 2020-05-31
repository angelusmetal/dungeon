package com.dungeon.engine.console;


import com.badlogic.gdx.graphics.Color;

@FunctionalInterface
public interface ConsoleOutput {
	void print(String output, Color color);
	default void print(String output) {
		print(output, Color.WHITE);
	}
}

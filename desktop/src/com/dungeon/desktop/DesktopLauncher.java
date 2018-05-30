package com.dungeon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dungeon.engine.resource.TextureResource;
import com.dungeon.game.Dungeon;
import com.moandjiezana.toml.Toml;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Read configuration from file
		Toml toml = new Toml().read(DesktopLauncher.class.getClassLoader().getResourceAsStream("config.toml"));
		LwjglApplicationConfiguration config = toml.getTable("application").to(LwjglApplicationConfiguration.class);
		new LwjglApplication(new Dungeon(toml), config);
	}
}

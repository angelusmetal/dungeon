package com.dungeon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dungeon.engine.EngineAdapter;
import com.dungeon.game.Dungeon;
import com.dungeon.game.resource.DungeonResources;
import com.moandjiezana.toml.Toml;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Read configuration from file
		Toml toml = new Toml().read(DesktopLauncher.class.getClassLoader().getResourceAsStream("config.toml"));
		LwjglApplicationConfiguration config = toml.getTable("application").to(LwjglApplicationConfiguration.class);
		DungeonResources.addLoaders();
		new EngineAdapter.Builder()
				.config(config)
				.listener(new Dungeon(toml))
				.assetsPath("assets/assets.conf")
				.launch();
	}
}

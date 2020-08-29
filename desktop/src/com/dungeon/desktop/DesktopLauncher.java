package com.dungeon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dungeon.engine.EngineAdapter;
import com.dungeon.engine.util.LwjglApplicationConfigurationBuilder;
import com.dungeon.game.Dungeon;
import com.dungeon.game.resource.DungeonResources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Read configuration from file
		Config gameConfig = ConfigFactory.load("config.conf");
		LwjglApplicationConfiguration appConfig = ConfigBeanFactory.create(gameConfig.getConfig("application"), LwjglApplicationConfigurationBuilder.class).build();
		DungeonResources.addLoaders();
		new EngineAdapter.Builder()
				.config(appConfig)
				.listener(new Dungeon(gameConfig))
				.assetsPath("assets/assets.conf")
				.launch();
	}
}

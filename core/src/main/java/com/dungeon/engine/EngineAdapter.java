package com.dungeon.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class EngineAdapter extends ApplicationAdapter {

	private final ApplicationListener listener;
	private final LwjglApplicationConfiguration config;

	private EngineAdapter(Builder builder) {
		this.listener = builder.listener;
		this.config = builder.config;
	}

	@Override
	public void create () {
		// Set the multiplexer as the main input to be able to host keys that must always work
		Gdx.input.setInputProcessor(Engine.inputMultiplexer);
		// And add an input stack to it and a mainKeyboardProcessor
		Engine.inputMultiplexer.addProcessor(Engine.inputStack);
		Engine.inputMultiplexer.addProcessor(Engine.mainKeyboardProcessor);

		// Initialize and push the main application listener
		listener.create();
		Engine.appListenerStack.push(listener);

//		initResources();
//		inputMultiplexer = new InputMultiplexer();
//		Game.devTools = new DevTools(inputMultiplexer);
//		devCommands = new DevCommands(Game.devTools);
//
//		// Set F12 to push & pop console input from the input processor
//		Game.devTools.addDeveloperHotkey(Input.Keys.ENTER, () -> {
//			Game.setDisplayConsole(true);
//			Engine.inputStack.push(Game.getConsole().getInputProcessor());
//		});
//		Game.getConsole().bindKey(Input.Keys.ENTER, () -> {
//			Game.getConsole().commandExecute();
//			Game.setDisplayConsole(false);
//			Engine.inputStack.pop();
//		});
//
//		Game.initialize(configuration);
//
//		characterSelection = new CharacterSelection();
//		characterSelection.initialize();
//
//		configureInput();
//
//		// Add developer hotkeys
//		Game.devTools.addDeveloperHotkeys();
//
//		// Start playing character selection music
//		Engine.audio.playMusic(Gdx.files.internal("audio/character_select.mp3"), 0f);
//
//		System.err.println("GL_MAX_TEXTURE_SIZE: " + Engine.getMaxTextureSize());
	}

	@Override
	public void render() {
		Engine.appListenerStack.render();
	}

	public static class Builder {
		private ApplicationListener listener;
		private LwjglApplicationConfiguration config;

		public Builder listener(ApplicationListener listener) {
			this.listener = listener;
			return this;
		}

		public Builder config(LwjglApplicationConfiguration config) {
			this.config = config;
			return this;
		}

		public void launch() {
			EngineAdapter adapter = new EngineAdapter(this);
			new LwjglApplication(adapter, config);
		}

	}

}

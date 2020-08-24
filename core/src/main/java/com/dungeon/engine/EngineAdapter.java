package com.dungeon.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dungeon.engine.console.ConsoleExpression;
import com.dungeon.engine.console.ConsoleVar;
import com.dungeon.engine.resource.Resources;

public class EngineAdapter extends ApplicationAdapter {

	private final ApplicationListener listener;
	private final LwjglApplicationConfiguration config;
	private final String assetsPath;

	private EngineAdapter(Builder builder) {
		this.listener = builder.listener;
		this.config = builder.config;
		this.assetsPath = builder.assetsPath;
	}

	@Override
	public void create () {
		InitializationAdapter initAdapter = new InitializationAdapter();
		initAdapter.create();
		Engine.appListenerStack.push(initAdapter);
		initAdapter.addInitTask("Initializing input...", () -> {
			// Set the multiplexer as the main input to be able to host keys that must always work
			Gdx.input.setInputProcessor(Engine.inputMultiplexer);
			// And add an input stack to it and a mainKeyboardProcessor
			Engine.inputMultiplexer.addProcessor(Engine.inputStack);
			Engine.inputMultiplexer.addProcessor(Engine.mainKeyboardProcessor);
		});
		initAdapter.addInitTask("Loading preferences...", Engine::loadPreferences);
		initAdapter.addInitTask("Configuring console...", () -> {
			// Bind engine expressions and variables
			Engine.console.bindExpression("playMusic", ConsoleExpression.of((String path) -> Engine.audio.playMusic(Gdx.files.internal(path))));
			Engine.console.bindExpression("stopMusic", ConsoleExpression.of(Engine.audio::stopMusic));

			Engine.console.bindVar(ConsoleVar.mutableColor("baseLight", Engine::getBaseLight, Engine::setBaseLight));
			Engine.console.bindVar(ConsoleVar.mutableFloat("specular", Engine::getSpecular, Engine::setSpecular));
			Engine.console.bindVar(ConsoleVar.mutableBoolean("normalMapEnabled", Engine::isNormalMapEnabled, Engine::setNormalMapEnabled));
			Engine.console.bindVar(ConsoleVar.mutableBoolean("shadowCastEnforced", Engine::isShadowCastEnforced, Engine::setShadowCastEnforced));
			Engine.console.bindVar(ConsoleVar.mutableBoolean("atlasForced", Engine::isAtlasForced, Engine::setAtlasForced));
			Engine.console.bindVar(ConsoleVar.readOnlyFloat("time", Engine::time));
			Engine.console.bindVar(ConsoleVar.mutableFloat("musicVolume", Engine.audio::getMusicVolume, Engine.audio::setMusicVolume));
		});
		initAdapter.addInitTask("Rebuilding atlas... (takes long only the first time)", Resources::initAtlas);
		initAdapter.addInitTask("Loading resources...", () -> {
			// Load resources
			Resources.loader.load(assetsPath);
		});
		initAdapter.addInitTask("Starting game...", () -> {
			Gdx.app.log("Initialization", "Starting game...");
			// Initialize and push the main application listener
			listener.create();
			Engine.appListenerStack.push(listener);
		});
	}

	@Override
	public void render() {
		Engine.addTime(Gdx.graphics.getDeltaTime());
		Engine.audio.update();
		// Defer rendering to the current stack frame
		Engine.appListenerStack.render();
	}

	@Override
	public void dispose() {
		Engine.appListenerStack.dispose();
		Engine.dispose();
	}

	public static class Builder {
		private ApplicationListener listener;
		private LwjglApplicationConfiguration config;
		private String assetsPath;

		public Builder listener(ApplicationListener listener) {
			this.listener = listener;
			return this;
		}

		public Builder config(LwjglApplicationConfiguration config) {
			this.config = config;
			return this;
		}

		public Builder assetsPath(String assetsPath) {
			this.assetsPath = assetsPath;
			return this;
		}

		public void launch() {
			EngineAdapter adapter = new EngineAdapter(this);
			new LwjglApplication(adapter, config);
		}

	}

}

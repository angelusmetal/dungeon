package com.dungeon.engine.resource;

import com.badlogic.gdx.graphics.Texture;

public class TextureResource implements Resource<Texture> {

	private final String filename;
	private Texture texture;

	public TextureResource(String filename) {
		this.filename = filename;
	}

	@Override
	public void load() {
		if (texture == null) {
			System.out.println("Loading texture " + filename + "...");
			texture = new Texture(filename);
		}
	}

	@Override
	public void unload() {
		if (texture != null) {
			System.out.println("Unloading texture " + filename + "...");
			texture.dispose();
		}
	}

	@Override
	public Texture get() {
		load();
		return texture;
	}
}

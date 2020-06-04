package com.dungeon.engine.resource.repository;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.dungeon.engine.resource.ResourceRepository;

public class Shaders {

	public static final ResourceRepository<ShaderProgram> repository = new ResourceRepository<>(Shaders::computeShader, ShaderProgram::dispose);

	public static ShaderProgram get(String key) {
		return repository.get(key);
	}

	public void put(String key, ShaderProgram resource) {
		repository.put(key, resource);
	}

	private static ShaderProgram computeShader(String name) {
		String[] scripts = name.split("\\|");
		ShaderProgram shaderProgram = new ShaderProgram(
				Gdx.files.internal("shaders/" + scripts[0]).readString(),
				Gdx.files.internal("shaders/" + scripts[1]).readString());
		if (!shaderProgram.isCompiled()) {
			throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
		}
		return shaderProgram;
	}

}

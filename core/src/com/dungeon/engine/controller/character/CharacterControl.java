package com.dungeon.engine.controller.character;

import com.dungeon.engine.entity.PlayerCharacter;

public abstract class CharacterControl {
	protected PlayerCharacter character;
	public abstract void bind();
	public abstract void unbind();
}

package com.dungeon.game;

import com.dungeon.engine.entity.PlayerCharacter;

public abstract class CharacterControllerMapper {
	protected PlayerCharacter character;
	abstract void bind();
	abstract void unbind();
}

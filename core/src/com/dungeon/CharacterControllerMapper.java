package com.dungeon;

import com.dungeon.character.PlayerCharacter;

public abstract class CharacterControllerMapper {
	protected PlayerCharacter character;
	abstract void bind();
	abstract void unbind();
}

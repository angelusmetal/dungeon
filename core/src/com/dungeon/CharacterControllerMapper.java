package com.dungeon;

import com.dungeon.character.Character;

public abstract class CharacterControllerMapper {
	protected Character character;
	abstract void bind();
	abstract void unbind();
}

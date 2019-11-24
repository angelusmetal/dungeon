package com.dungeon.game.character.merchant;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.CreatureEntity;

import java.util.Arrays;
import java.util.List;

public class MerchantFactory {
	private final List<String> greetPhrases = Arrays.asList(
			"Greetings adventurer!",
			"Take a look at my wares",
			"Look what we have here!",
			"You look like you can use some help",
			"Meow meow meow",
			"All of my merchandise it top notch",
			"Prrrrrrrrrrrrrroblem?");
	public Entity kat(Vector2 origin, EntityPrototype prototype) {
		return new CreatureEntity(origin, prototype) {
			@Override protected void onSignal(Entity emitter) {
				say(greetPhrases);
			}
		};
	}


}

package com.dungeon.game.character.npc;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.object.shop.ShopItem;

import java.util.Arrays;
import java.util.List;

public class MerchantFactory {
	private final List<String> greetPhrases = Arrays.asList(
			"Greetings adventurer!",
			"Take a look at my wares",
			"Look what we have here!",
			"You look like you can use some help",
			"Meow meow meow",
			"All of my merchandise is top notch",
			"Prrrrrrrrrrrrrroblem?");

	private final List<String> buyPhrases = Arrays.asList(
			"Great pick!",
			"Thanks for buying!",
			"I would have picked the same",
			"I can tell you're very smart",
			"Come back soon!");
	private final List<String> cantBuyPhrases = Arrays.asList(
			"Seems like someone doesn't have money",
			"Short on cash, huh?",
			"Such a pity",
			"Come back when you got money");

	private final List<ShopItem> items = Arrays.asList(
//			new ShopItem("weapon_sword", 75),
//			new ShopItem("weapon_cat_staff", 80),
//			new ShopItem("weapon_green_staff", 75),
			new ShopItem("random_weapon", 0),
			new ShopItem("random_weapon", 0),
			new ShopItem("random_weapon", 0),
			new ShopItem("potion_health_small", 25),
			new ShopItem("potion_health_large", 75)
	);

	public Entity kat(Vector2 origin, EntityPrototype prototype) {
		return new MerchantEntity(origin, prototype,
				Resources.animations.get("kat_idle"),
				Resources.animations.get("kat_talk"),
				greetPhrases,
				buyPhrases,
				cantBuyPhrases,
				items);
	}

	public Entity wanderer(Vector2 origin, EntityPrototype prototype) {
		return new NpcEntity(origin, prototype,
				Resources.animations.get("kat_idle"),
				Resources.animations.get("kat_talk"),
				Arrays.asList("Greetings adventurer", "This is the dungeon of Arkind", "A foul place filled with monsters", "Enter at your own risk"));
	}

	public Entity phillipe(Vector2 origin, EntityPrototype prototype) {
		return new NpcEntity(origin, prototype,
				Resources.animations.get("ghost_hover"),
				Resources.animations.get("ghost_hover"),
				Arrays.asList(
						"I don't think she'll be back, will she?",
						"What a fool I've been",
						"It's been so long",
						"I can't remember her face anymore",
						"My name is Phillipe"
				));
	}

}

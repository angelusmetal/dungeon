package com.dungeon.game.character.merchant;

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
			new ShopItem("weapon_sword", 75),
			new ShopItem("weapon_cat_staff", 80),
			new ShopItem("weapon_green_staff", 75),
			new ShopItem("health_powerup", 25)
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


}

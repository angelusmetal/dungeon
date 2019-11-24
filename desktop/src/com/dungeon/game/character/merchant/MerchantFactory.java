package com.dungeon.game.character.merchant;

import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.object.shop.ShopItem;
import com.dungeon.game.object.shop.ShopItemEntity;
import com.dungeon.game.resource.Resources;

import java.util.Arrays;
import java.util.Collections;
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
		MerchantEntity kat = new MerchantEntity(origin, prototype,
				Resources.animations.get("kat_idle"),
				Resources.animations.get("kat_talk"),
				greetPhrases,
				buyPhrases,
				cantBuyPhrases,
				items);
		// Shuffle item collection and sell the first 2
		Collections.shuffle(items);
		ShopItemEntity item1 = new ShopItemEntity(items.get(0), origin.cpy().add(-20f, -20f), kat);
		ShopItemEntity item2 = new ShopItemEntity(items.get(1), origin.cpy().add(20f, -20f), kat);
		Engine.entities.add(item1);
		Engine.entities.add(item2);
		return kat;
	}


}

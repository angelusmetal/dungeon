package com.dungeon.game.character.npc;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.object.shop.ShopItem;
import com.dungeon.game.object.shop.ShopItemEntity;

import java.util.Collections;
import java.util.List;

public class MerchantEntity extends NpcEntity {
	private final List<String> buyPhrases;
	private final List<String> cantBuyPhrases;

	public MerchantEntity(Vector2 origin, EntityPrototype prototype,
						  Animation<Material> idleAnimation, Animation<Material> talkAnimation,
						  List<String> greetPhrases, List<String> buyPhrases, List<String> cantBuyPhrases,
						  List<ShopItem> items) {
		super(origin, prototype, idleAnimation, talkAnimation, greetPhrases);
		this.buyPhrases = buyPhrases;
		this.cantBuyPhrases = cantBuyPhrases;
		Collections.shuffle(items);
		ShopItemEntity item1 = new ShopItemEntity(items.get(0), origin.cpy().add(-20f, -30f), this);
		ShopItemEntity item2 = new ShopItemEntity(items.get(1), origin.cpy().add(20f, -30f), this);
		Engine.entities.add(item1);
		Engine.entities.add(item2);
	}

	public void bought(Entity emitter) {
		sayTo(emitter, Rand.pick(buyPhrases));
	}

	public void cantBuy(Entity emitter) {
		sayTo(emitter, Rand.pick(cantBuyPhrases));
	}

}

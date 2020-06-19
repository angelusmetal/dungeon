package com.dungeon.game.object.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.Game;
import com.dungeon.game.character.npc.MerchantEntity;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;

public class ShopItemEntity extends DungeonEntity {

	private static final Vector2 PRICE_OFFSET = new Vector2(0, -15);
	private final int price;
	private final String item;
	private final MerchantEntity merchant;

	public ShopItemEntity(ShopItem item, Vector2 origin, MerchantEntity merchant) {
		// We first build this as a clone of the item
		super(Game.build(item.getItemType(), origin));
		this.item = item.getItemType();
		this.price = item.getBasePrice();
		this.merchant = merchant;

		// But then make some tweaks
		this.canBlock = false;
		// Add text for price
		OverlayText overlayText = Game.text(origin, "" + price, Color.GOLD)
				.bindTo(this, PRICE_OFFSET, true);
		Engine.overlayTexts.add(overlayText);
	}

	@Override protected void onSignal(Entity emitter) {
		// Only if emitter is a player character
		if (emitter instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity) emitter;
			// Only if player can pay
			if (playerEntity.getPlayer().getGold() > price) {
				playerEntity.getPlayer().subtractGold(price);
				Engine.entities.add(Game.build(item, getOrigin()));
				// Remove this placeholder
				expire();
				// TODO replace this with a specialized method
				merchant.bought(emitter);
			} else {
				merchant.cantBuy(emitter);
			}
		}
	}

}

package com.dungeon.game.object.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.OverlayText;
import com.dungeon.engine.entity.Entity;
import com.dungeon.game.Game;
import com.dungeon.game.character.npc.MerchantEntity;
import com.dungeon.game.combat.Weapon;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.object.weapon.WeaponFactory;
import com.dungeon.game.resource.DungeonResources;

public class ShopItemEntity extends DungeonEntity {

	private static final Vector2 PRICE_OFFSET = new Vector2(0, -2);
	private final int price;
	private final MerchantEntity merchant;
	private final Entity item;

	public ShopItemEntity(ShopItem item, Vector2 origin, MerchantEntity merchant) {
		// We first build this as a clone of the item
		super(Game.build(item.getItemType(), origin));
		if (item.getItemType().equals("random_weapon")) {
			Weapon weapon = new WeaponFactory().buildRandom(Game.getEnvironment().getTier());
			this.item = new WeaponFactory().buildWeaponEntity(getOrigin(), DungeonResources.prototypes.get("random_weapon"),() -> weapon);
			// Update both the item and the placeholder animation from the weapon
			this.item.setAnimation(weapon.getHudAnimation(), Engine.time());
			setAnimation(weapon.getHudAnimation(), Engine.time());
			this.price = weapon.getPrice();
		} else {
			this.item = Game.build(item.getItemType(), getOrigin());
			this.price = item.getBasePrice();
		}
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
				Engine.entities.add(item);
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

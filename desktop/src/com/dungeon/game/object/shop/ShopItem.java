package com.dungeon.game.object.shop;

public class ShopItem {
	private final String itemType;
	private final int basePrice;

	public ShopItem(String itemType, int price) {
		this.itemType = itemType;
		this.basePrice = price;
	}

	public String getItemType() {
		return itemType;
	}

	public int getBasePrice() {
		return basePrice;
	}
}

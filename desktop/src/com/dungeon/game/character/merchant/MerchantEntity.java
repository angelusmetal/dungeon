package com.dungeon.game.character.merchant;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.Game;
import com.dungeon.game.entity.CreatureEntity;
import com.dungeon.game.object.shop.ShopItem;

import java.util.List;

public class MerchantEntity extends CreatureEntity {
	private final Animation<TextureRegion> idleAnimation;
	private final Animation<TextureRegion> talkAnimation;
	private final List<String> greetPhrases;
	private final List<String> buyPhrases;
	private final List<String> cantBuyPhrases;
	private final List<ShopItem> items;

	public MerchantEntity(Vector2 origin, EntityPrototype prototype,
						  Animation<TextureRegion> idleAnimation, Animation<TextureRegion> talkAnimation,
						  List<String> greetPhrases, List<String> buyPhrases, List<String> cantBuyPhrases,
						  List<ShopItem> items) {
		super(origin, prototype);
		this.idleAnimation = idleAnimation;
		this.talkAnimation = talkAnimation;
		this.greetPhrases = greetPhrases;
		this.buyPhrases = buyPhrases;
		this.cantBuyPhrases = cantBuyPhrases;
		this.items = items;
	}

	@Override protected void think() {
		if (getAnimation() == talkAnimation && nextTalk < Engine.time()) {
			setAnimation(idleAnimation, 0f);
		}
	}
	@Override protected void onSignal(Entity emitter) {
		sayTo(emitter, greetPhrases);
	}

	public void bought(Entity emitter) {
		sayTo(emitter, buyPhrases);
	}

	public void cantBuy(Entity emitter) {
		sayTo(emitter, cantBuyPhrases);
	}

	private void sayTo(Entity emitter, List<String> text) {
		setAnimation(talkAnimation, 0f);
		// Make the merchant look at the character who interacted with them
		getDrawScale().x = emitter.getOrigin().x - getOrigin().x < 0 ? -1 : 1;
		say(text);
	}

}

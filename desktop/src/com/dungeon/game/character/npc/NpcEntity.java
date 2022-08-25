package com.dungeon.game.character.npc;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Material;
import com.dungeon.game.entity.CreatureEntity;

import java.util.List;

import static java.lang.Math.abs;

public class NpcEntity extends CreatureEntity {
	protected final Animation<Material> idleAnimation;
	protected final Animation<Material> talkAnimation;
	// TODO This should become a full-fledge conversation system
	protected final List<String> greetPhrases;
	protected int greetPhrase = 0;
	protected float talkUntil = 0f;

	public NpcEntity(Vector2 origin, EntityPrototype prototype,
					 Animation<Material> idleAnimation, Animation<Material> talkAnimation,
					 List<String> greetPhrases) {
		super(origin, prototype);
		this.idleAnimation = idleAnimation;
		this.talkAnimation = talkAnimation;
		this.greetPhrases = greetPhrases;
	}

	@Override protected void think() {
		if (getAnimation() == talkAnimation && talkUntil < Engine.time()) {
			setAnimation(idleAnimation, 0f);
		}
	}
	@Override protected void onSignal(Entity emitter) {
//		greetPhrase %= greetPhrases.size();
		if (greetPhrase < greetPhrases.size()) {
			if (sayTo(emitter, greetPhrases.get(greetPhrase))) {
				greetPhrase++;
			}
		}
	}

	protected boolean sayTo(Entity emitter, String text) {
		// Make the npc look at the character who interacted with them
		getDrawScale().x = abs(getDrawScale().x) * (emitter.getOrigin().x - getOrigin().x < 0 ? -1 : 1);
		if (nextTalk < Engine.time()) {
			talkUntil = Engine.time() + 1f;
			setAnimation(talkAnimation, 0f);
			say(text);
			return true;
		} else {
			return false;
		}
	}

}

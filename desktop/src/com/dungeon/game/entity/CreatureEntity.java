package com.dungeon.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.movement.Movable;
import com.dungeon.engine.render.Drawable;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.TimeGate;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;

import java.util.List;

public abstract class CreatureEntity extends DungeonEntity implements Movable, Drawable {

	private Vector2 aim = new Vector2(0, 0);
	private Vector2 face = new Vector2(0, 0);
	protected float nextTalk = 0;

	protected TimeGate actionGate = new TimeGate(); // Default is fire every 0.25 seconds

	public CreatureEntity(Vector2 origin, EntityPrototype prototype) {
		super(prototype, origin);
	}

	public void aim(Vector2 vector) {
		aim.set(vector);
	}

	public void aimTo(Vector2 origin) {
		aim.set(origin.x - getOrigin().x, origin.y - getOrigin().y).setLength2(1f);
	}

	public Vector2 getAim() {
		return aim;
	}

	public void face(Vector2 vector) {
		face.set(vector);
	}

	public Vector2 getFace() {
		return face;
	}

	public void drawHealthbar(SpriteBatch batch, ViewPort viewPort) {
		// Draw health bar
//		viewPort.draw(batch, GameState.getTilesetManager().getHudTileset().HEALTH_BAR, getOrigin().x - getDrawOffset().x, getOrigin().y - getDrawOffset().y + getBoundingBox().y + z, getBoundingBox().x * health/maxHealth, 2);
//		viewPort.draw(batch, GameState.getTilesetManager().getHudTileset().HEALTH_BAR, getOrigin().x - getBoundingBox().x / 2, getOrigin().y + getBoundingBox().y / 2 + 4 + z, getBoundingBox().x * health/maxHealth, 2);
	}

	public void say(String text) {
		if (Engine.time() > nextTalk) {
			nextTalk = Engine.time() + 3f;
			Game.say(this, text);
		}
	}

	public void say(String text, float chance) {
		if (Rand.chance(chance)) {
			say(text);
		}
	}

	public void say(List<String> text) {
		if (Engine.time() > nextTalk) {
			nextTalk = Engine.time() + 3;
			Game.say(this, Rand.pick(text));
		}
	}

	protected void say(List<String> text, float chance) {
		if (Rand.chance(chance)) {
			say(text);
		}
	}
	protected void shout(String text) {
		if (Engine.time() > nextTalk) {
			nextTalk = Engine.time() + 3;
			Game.shout(this, text);
		}
	}

	protected void shout(String text, float chance) {
		if (Rand.chance(chance)) {
			shout(text);
		}
	}

	protected void shout(List<String> text) {
		if (Engine.time() > nextTalk) {
			nextTalk = Engine.time() + 3;
			Game.shout(this, Rand.pick(text));
		}
	}

	protected void shout(List<String> text, float chance) {
		if (Rand.chance(chance)) {
			shout(text);
		}
	}

}

package com.dungeon.engine.resource;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;

public class PrototypeDef {
	String name;
	String animation;
	int[] boundingBox;
	String color;
	int[] drawOffset;
	int health;
	float timeToLive;
	float zAccel;
	float zSpeed;
	int zIndex;
	float[] hOscillate;
	float[] zOscillate;
	float fadeOut;
	boolean fadeOutLight;

	public void load(String name) {
		this.name = name;
		EntityPrototype prototype = new EntityPrototype();
		if (animation != null) {
			prototype.animation(ResourceManager.getAnimation(animation));
		}
		if (boundingBox != null) {
			prototype.boundingBox(new Vector2(boundingBox[0], boundingBox[1]));
		}
		if (color != null) {
			prototype.color(Color.valueOf(color));
		}
		if (drawOffset != null) {
			prototype.drawOffset(new Vector2(drawOffset[0], drawOffset[1]));
		}
		if (health != 0) {
			prototype.health(health);
		}
		if (timeToLive != 0) {
			prototype.timeToLive(timeToLive);
		}
		if (zAccel != 0) {
			prototype.with(Traits.zAccel(zAccel));
		}
		if (zSpeed != 0) {
			prototype.zSpeed(zSpeed);
		}
		if (zIndex != 0) {
			prototype.zIndex(zIndex);
		}
		if (fadeOut != 0) {
			prototype.with(Traits.fadeOut(fadeOut));
		}
		if (fadeOutLight) {
			prototype.with(Traits.fadeOutLight());
		}
		if (hOscillate != null) {
			prototype.with(Traits.hOscillate(hOscillate[0], hOscillate[1]));
		}
		if (zOscillate != null) {
			prototype.with(Traits.zOscillate(zOscillate[0], zOscillate[1]));
		}
		ResourceManager.loadPrototype(name, prototype);
	}

}

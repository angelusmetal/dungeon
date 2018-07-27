package com.dungeon.engine.resource;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class PrototypeDef {
	String name;
	String animation;
	int[] boundingBox;
	String color;
	int[] drawOffset;
	Float friction;
	int health;
	Float knockback;
	String[] light;
	float timeToLive;
	float zAccel;
	float zSpeed;
	int zIndex;
	float[] hOscillate;
	float[] zOscillate;
	float fadeOut;
	Boolean fadeOutLight;
	Boolean solid;
	Boolean canBeHit;
	Boolean canBeHurt;

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
		if (friction != null) {
			prototype.friction(friction);
		}
		if (health != 0) {
			prototype.health(health);
		}
		if (knockback != null) {
			prototype.knockback(knockback);
		}
		if (light != null) {
			if (light.length < 3) {
				throw new RuntimeException("light must have at least 3 parameters");
			}
			float diameter = Float.parseFloat(light[0]);
			Color c = Color.valueOf(light[1]);
			Texture t = getLightTexture(light[2]);
			List<Consumer<Light>> traits = new ArrayList<>();
			for (int i = 3; i < light.length; ++i) {
				traits.add(getLightTrait(light[i]));
			}
			prototype.light(new Light(diameter, c, t, traits));
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
		if (fadeOutLight != null && fadeOutLight) {
			prototype.with(Traits.fadeOutLight());
		}
		if (hOscillate != null) {
			prototype.with(Traits.hOscillate(hOscillate[0], hOscillate[1]));
		}
		if (zOscillate != null) {
			prototype.with(Traits.zOscillate(zOscillate[0], zOscillate[1]));
		}
		if (solid != null) {
			prototype.solid(solid);
			prototype.canBeHit(solid);
			prototype.canBeHurt(solid);
		}
		if (canBeHit != null) {
			prototype.canBeHit(canBeHit);
			prototype.canBeHurt(canBeHit);
		}
		if (canBeHurt != null) {
			prototype.canBeHurt(canBeHurt);
		}
		ResourceManager.loadPrototype(name, prototype);
	}

	private Texture getLightTexture(String name) {
		if ("NORMAL".equals(name)) {
			return Light.NORMAL;
		} else if ("RAYS".equals(name)) {
			return Light.RAYS;
		} else if ("FLARE".equals(name)) {
			return Light.FLARE;
		} else {
			throw new RuntimeException("light texture '" + name + "' not recognized");
		}
	}

	private Consumer<Light> getLightTrait(String name) {
		if ("torchlight".equals(name)) {
			return Light.torchlight();
		} else if ("rotateSlow".equals(name)) {
			return Light.rotateSlow();
		} else if ("rotateMedium".equals(name)) {
			return Light.rotateMedium();
		} else if ("rotateFast".equals(name)) {
			return Light.rotateFast();
		} else if ("oscillate".equals(name)) {
			return Light.oscillate();
		} else {
			throw new RuntimeException("light trait '" + name + "' not recognized");
		}
	}

}

package com.dungeon.game.object.torch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;

public class TorchFactory implements EntityFactory.EntityTypeFactory {

	private static final String IDLE = "torch_idle";

	private static final Vector2 BOUNDING_BOX = new Vector2(10, 10);
	private static final Vector2 DRAW_OFFSET = new Vector2(16, 16);

	private final EntityPrototype prototype;

	public TorchFactory() {
		Light light = new Light(80, new Color(1, 0.7f, 0.2f, 1), Light.NORMAL_TEXTURE, Light::torchlight, Light::noRotate);
		Animation<TextureRegion> animation = ResourceManager.getAnimation(IDLE);
		prototype = new EntityPrototype()
				.animation(animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.light(light);
	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(origin, prototype) {
			@Override
			public boolean isSolid() {
				return false;
			}
		};
	}
}

package com.dungeon.game.object.exit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.DrawFunction;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class ExitPlatformFactory implements EntityFactory.EntityTypeFactory {

	public static final String CLOUD = "cloud";
	public static final String PLATFORM = "exit_platform_idle";

	private static final Vector2 BOUNDING_BOX = new Vector2(64, 64);
	private static final Vector2 DRAW_OFFSET = new Vector2(32, 32);

	private final EntityPrototype prototype;
	private final EntityPrototype cloud;

	public ExitPlatformFactory() {
		Animation<TextureRegion> animation = ResourceManager.getAnimation(PLATFORM);
		Animation<TextureRegion> specAnimation = ResourceManager.getAnimation(CLOUD);

		Light light = new Light(300, Color.BLUE, Light.RAYS_TEXTURE, Light::torchlight, Light::rotateSlow);
		Texture cloudTexture = ResourceManager.getTexture("cloud.png");

		cloud = new EntityPrototype()
				.animation(specAnimation)
				.boundingBox(new Vector2(1, 1))
				.drawOffset(new Vector2(32, 32))
				.timeToLive(5f)
				.color(Color.WHITE)
				.with(Traits.fadeOut(0.5f))
				.drawFunction(DrawFunction.rotateRandom(cloudTexture, 50f))
		;

		prototype = new EntityPrototype()
				.animation(animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.light(light)
				.with(Traits.generator(0.1f, (generator) -> {
					Entity particle = new Entity(generator.getPos(), cloud);
					particle.impulse(Rand.between(-30, 30), Rand.between(-30, 30));
					return particle;
				}))
				.zIndex(-1);

	}

	@Override
	public Entity build(Vector2 origin) {
		return new Entity(origin, prototype) {
			boolean exited = false;
			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (!exited && entity instanceof PlayerEntity) {
					exited = true;
					GameState.exitLevel();
				}
				return true;
			}
		};
	}

}

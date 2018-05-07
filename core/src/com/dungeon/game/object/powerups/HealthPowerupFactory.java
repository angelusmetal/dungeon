package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.FillSheet;

public class HealthPowerupFactory implements EntityFactory.EntityTypeFactory {

	private static final Vector2 BOUNDING_BOX = new Vector2(10, 10);
	private static final Vector2 DRAW_OFFSET = new Vector2(10, 10);

	private final EntityPrototype powerupPrototype;
	private final EntityPrototype specPrototype;
	private final EntityPrototype vanishPrototype;

	public HealthPowerupFactory() {
		Animation<TextureRegion> animation = ResourceManager.instance().getAnimation(PowerupsSheet.HEALTH, PowerupsSheet::health);
		Animation<TextureRegion> specAnimation = ResourceManager.instance().getAnimation(FillSheet.FILL, FillSheet::fill);

		Light light = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::oscillating, Light::rotateFast);
		Light vanishLight = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::torchlight, Light::rotateFast);

		specPrototype = new EntityPrototype()
				.animation(specAnimation)
				.boundingBox(new Vector2(1, 1))
				.drawOffset(new Vector2(1, 1))
				.zSpeed(50)
				.timeToLive(1f)
				.color(Color.RED)
				.with(Traits.fadeOut(0.8f))
				.with(Traits.zAccel(100))
				.with(Traits.hOscillate(10, 5f));

		vanishPrototype = new EntityPrototype()
				.animation(animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.timeToLive(1.5f)
				.light(vanishLight)
				.with(Traits.fadeOut(0.5f))
				.with(Traits.fadeOutLight())
				.with(Traits.zAccel(10));

		powerupPrototype = new EntityPrototype()
				.animation(animation)
				.boundingBox(BOUNDING_BOX)
				.drawOffset(DRAW_OFFSET)
				.light(light)
				.with(Traits.zOscillate(3, 8f))
				.with(Traits.generator(0.05f, (powerup) -> {
					Entity spec = new Entity(powerup.getPos(), specPrototype);
					spec.getPos().x += Rand.between(-8, 8);
					spec.setZPos(Rand.between(2, 10));
					spec.impulse(Rand.between(-10, 10), 0);
					return spec;
				}));
	}

	@Override
	public Entity build(Vector2 origin) {
		Entity e = new Entity(origin, powerupPrototype) {

			@Override
			protected boolean onEntityCollision(Entity entity) {
				if (entity instanceof PlayerCharacter) {
					PlayerCharacter character = (PlayerCharacter) entity;
					character.heal(25);
					expire();
					return true;
				} else {
					return false;
				}
			}

			protected void onExpire() {
				Entity vanish = new Entity(getPos(), vanishPrototype) {};
				vanish.setZPos(z);
				GameState.addEntity(vanish);
				for (int i = 0; i < 50; ++i) {
					Entity spec = new Entity(getPos(), specPrototype) {};
					spec.getPos().x += Rand.between(-8, 8);
					spec.setZPos(Rand.between(2, 10));
					spec.impulse(Rand.between(-100, 100), Rand.between(-100, 100));
					GameState.addEntity(spec);
				}
			}
		};
		e.setZPos(4);
		return e;
	}
}

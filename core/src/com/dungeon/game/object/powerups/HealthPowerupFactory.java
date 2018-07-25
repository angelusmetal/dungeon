package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.render.Light;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;
import com.dungeon.game.tileset.FillSheet;

public class HealthPowerupFactory implements EntityFactory.EntityTypeFactory {

	private static final String HEALTH = "powerup_health";

	private static final Vector2 BOUNDING_BOX = new Vector2(10, 10);
	private static final Vector2 DRAW_OFFSET = new Vector2(10, 10);

	private final EntityPrototype powerupPrototype;
	private final EntityPrototype specPrototype;
	private final EntityPrototype vanishPrototype;

	public HealthPowerupFactory() {
		Animation<TextureRegion> animation = ResourceManager.getAnimation(HEALTH);

		Light light = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::oscillating, Light::rotateFast);
		Light vanishLight = new Light(192, new Color(1, 0.1f, 0.2f, 1), Light.RAYS_TEXTURE, Light::torchlight, Light::rotateFast);

		specPrototype = ResourceManager.getPrototype("particle_health_spec");
		vanishPrototype = ResourceManager.getPrototype("health_powerup_fade")
				// TODO Add these to prototypes.yaml
				.light(vanishLight);

		powerupPrototype = ResourceManager.getPrototype("health_powerup")
				// TODO Add these to prototypes.yaml
				.light(light)
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
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					int amount = 25;
					character.heal(amount);
					expire();
					character.getPlayer().getConsole().log("Healed for " + amount, Color.GOLD);
					character.getPlayer().getRenderer().beginMotionBlur();
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

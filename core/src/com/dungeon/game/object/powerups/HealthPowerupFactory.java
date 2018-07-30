package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.Traits;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityFactory;
import com.dungeon.game.state.GameState;

public class HealthPowerupFactory implements EntityFactory.EntityTypeFactory {

	private final EntityPrototype powerupPrototype;
	private final EntityPrototype specPrototype;
	private final EntityPrototype vanishPrototype;

	public HealthPowerupFactory() {
		specPrototype = ResourceManager.getPrototype("particle_health_spec");
		vanishPrototype = ResourceManager.getPrototype("health_powerup_fade");
		powerupPrototype = ResourceManager.getPrototype("health_powerup")
				// TODO Add these to prototypes.yaml
				.with(Traits.generator(0.05f, (powerup) -> {
					Entity spec = new Entity(specPrototype, powerup.getOrigin());
					spec.getOrigin().x += Rand.between(-8, 8);
					spec.setZPos(Rand.between(2, 10));
					spec.impulse(Rand.between(-10, 10), 0);
					return spec;
				}));
	}

	@Override
	public Entity build(Vector2 origin) {
		Entity e = new Entity(powerupPrototype, origin) {

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
				Entity vanish = new Entity(vanishPrototype, getOrigin()) {};
				vanish.setZPos(z);
				GameState.addEntity(vanish);
				for (int i = 0; i < 50; ++i) {
					Entity spec = new Entity(specPrototype, getOrigin()) {};
					spec.getOrigin().x += Rand.between(-8, 8);
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

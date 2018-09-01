package com.dungeon.game.object.powerups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.resource.Resources;

public class HealthPowerupFactory implements EntityTypeFactory {

	private final EntityPrototype powerupPrototype;
	private final EntityPrototype specPrototype;
	private final EntityPrototype vanishPrototype;

	public HealthPowerupFactory() {
		specPrototype = Resources.prototypes.get("particle_health_spec");
		vanishPrototype = Resources.prototypes.get("health_powerup_fade");
		powerupPrototype = Resources.prototypes.get("health_powerup");
	}

	@Override
	public Entity build(Vector2 origin) {
		Entity e = new DungeonEntity(powerupPrototype, origin) {

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
				Engine.entities.add(vanish);
				// TODO Move this as well to prototypes.toml
				for (int i = 0; i < 50; ++i) {
					Entity spec = new Entity(specPrototype, getOrigin()) {};
					spec.getOrigin().x += Rand.between(-8, 8);
					spec.setZPos(Rand.between(2, 10));
					spec.impulse(Rand.between(-100, 100), Rand.between(-100, 100));
					Engine.entities.add(spec);
				}
			}
		};
		e.setZPos(8);
		return e;
	}
}

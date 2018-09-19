package com.dungeon.game.object.props;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.Resources;

public class FurnitureFactory {

	public Entity chest(Vector2 origin, EntityPrototype prototype) {
		Animation<TextureRegion> chestOpening = Resources.animations.get("chest_opening");
		return new DungeonEntity(prototype, origin) {
			boolean opened = false;
			@Override
			public void onSignal(Entity emitter) {
				if (!opened) {
					opened = true;
					setCurrentAnimation(chestOpening);
					Entity loot = Game.build(Game.createLoot(), getOrigin());
					loot.setZPos(15);
					Engine.entities.add(loot);
				}
			}
		};
	}

	public Entity coin(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					character.getPlayer().addGold(1);
					character.getPlayer().getConsole().log("Picked up gold!", Color.GOLD);
					character.getPlayer().getRenderer().addParticle(
							getOrigin().cpy().add(0, getZPos()),
							new Vector2(4, 190),
							new Vector2(Rand.between(50, 100), 0).rotate(Rand.between(0, 360)),
							getCurrentAnimation().getAnimation());
					expire();
					return true;
				}
				return false;
			}
		};
	}

}

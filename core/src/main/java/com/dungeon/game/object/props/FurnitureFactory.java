package com.dungeon.game.object.props;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.level.entity.EntityType;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.state.GameState;

public class FurnitureFactory {

	public final EntityTypeFactory chest;
	public final EntityTypeFactory coin;

	public FurnitureFactory() {
		EntityPrototype chestPrototype = Resources.prototypes.get("chest");
		EntityPrototype coinPrototype = Resources.prototypes.get("coin");

		Animation<TextureRegion> chestOpening = Resources.animations.get("chest_opening");

		chest = origin -> new Entity(chestPrototype, origin) {
			@Override public void onSignal(Entity emitter) {
				setCurrentAnimation(chestOpening);
				Entity loot = GameState.build(GameState.createLoot(), getOrigin());
				loot.setZPos(15);
				GameState.entities.add(loot);
			}
		};

		coin = origin -> new Entity(coinPrototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					character.getPlayer().addGold(1);
					character.getPlayer().getConsole().log("Picked up gold!", Color.GOLD);
					expire();
					return true;
				}
				return false;
			}
		};

	}

}

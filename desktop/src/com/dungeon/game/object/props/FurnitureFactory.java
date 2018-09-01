package com.dungeon.game.object.props;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.entity.factory.EntityTypeFactory;
import com.dungeon.game.resource.Resources;
import com.dungeon.engine.Engine;

public class FurnitureFactory {

	public final EntityTypeFactory chest;
	public final EntityTypeFactory coin;

	public FurnitureFactory() {
		EntityPrototype chestPrototype = Resources.prototypes.get("chest");
		EntityPrototype coinPrototype = Resources.prototypes.get("coin");

		Animation<TextureRegion> chestOpening = Resources.animations.get("chest_opening");

		chest = origin -> new DungeonEntity(chestPrototype, origin) {
			@Override public void onSignal(Entity emitter) {
				setCurrentAnimation(chestOpening);
				Entity loot = Game.build(Game.createLoot(), getOrigin());
				loot.setZPos(15);
				Engine.entities.add(loot);
			}
		};

		coin = origin -> new DungeonEntity(coinPrototype, origin) {
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

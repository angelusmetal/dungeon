package com.dungeon.game.object.props;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.ui.CoinsWidget;

public class FurnitureFactory {

	public Entity coin(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					character.getPlayer().addGold(1);
					character.getPlayer().getConsole().log("Picked up gold!", Color.GOLD);
					CoinsWidget coinsWidget = character.getPlayer().getRenderer().getHudStage().getCoinsWidget();
					Vector2 origin = getOrigin().cpy().add(0, getZPos());
					Vector2 destination = new Vector2(coinsWidget.getX(), coinsWidget.getY());
					character.getPlayer().getRenderer().getHudStage().addRandomQuadraticParticle(origin, destination, getAnimation());
					expire();
					return true;
				}
				return false;
			}
		};
	}

}

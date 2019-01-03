package com.dungeon.game.object.props;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.entity.Metronome;
import com.dungeon.engine.ui.particle.LinearParticle;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.engine.ui.particle.PathParticle;
import com.dungeon.game.ui.CoinsWidget;

import static com.dungeon.engine.util.Util.randVect;

public class FurnitureFactory {

	public Entity coin(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					character.getPlayer().getConsole().log("Picked up gold!", Color.GOLD);
					CoinsWidget coinsWidget = character.getPlayer().getRenderer().getHudStage().getCoinsWidget();
					Vector2 origin = getOrigin().cpy().add(0, getZPos());
					Vector2 destination = coinsWidget.getCenter();
					Bezier<Vector2> path = character.getPlayer().getRenderer().getHudStage().randQuadratic(origin, destination);

					PathParticle particle = new PathParticle(path, getAnimation(),1f) {
						Metronome sparkGenerator;
						{
							sparkGenerator = new Metronome(0.025f, () -> {
								LinearParticle spark = new LinearParticle(this.origin, randVect(10, 30), this.animation, 0.5f) {
									@Override public void update() {
										getColor().a = (1 - (Engine.time() - startTime) / duration) * 0.1f;
									}
								};
								character.getPlayer().getRenderer().getHudStage().addParticle(spark);
							});
						}
						@Override public void update() {
							sparkGenerator.doAtInterval();
						}
						@Override public void expire(){
							character.getPlayer().addGold(1);
						}
					};
					character.getPlayer().getRenderer().getHudStage().addParticle(particle);
					expire();
					return true;
				}
				return false;
			}
		};
	}

}

package com.dungeon.game.object.props;

import static com.dungeon.engine.util.Util.randVect;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.entity.EntityPrototype;
import com.dungeon.engine.ui.particle.LinearParticle;
import com.dungeon.engine.ui.particle.PathParticle;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.TimeGradient;
import com.dungeon.game.Game;
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.Resources;
import com.dungeon.game.ui.CoinsWidget;

public class FurnitureFactory {

	Sound soundCoin = Resources.sounds.get("audio/sound/coin.ogg");
	Sound soundFurnitureHit = Resources.sounds.get("audio/sound/hit_dry.ogg");
	Sound soundFurnitureBreak = Resources.sounds.get("audio/sound/break_wood.ogg");

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
							sparkGenerator = new Metronome(1f / 30f, () -> {
								TimeGradient gradient = TimeGradient.fadeOut(startTime, duration);
								LinearParticle spark = new LinearParticle(this.origin, randVect(10, 30), this.animation, 0.5f) {
									@Override public void update() {
										getColor().a = gradient.get() * 0.1f;
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
			@Override public void onGroundBounce(float zSpeed) {
				Engine.audio.playSound(soundCoin, getOrigin(), zSpeed / 100f, 0.05f);
			}
		};
	}

	public Entity furniture(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public void onHit() {
				Engine.audio.playSound(soundFurnitureBreak, getOrigin(), 1f, 0.05f);
			}
			@Override public void onExpire() {
				Engine.audio.playSound(soundFurnitureBreak, getOrigin(), 1f, 0.05f);
			}
		};
	}

}

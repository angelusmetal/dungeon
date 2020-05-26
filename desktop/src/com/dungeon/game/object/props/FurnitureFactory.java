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
import com.dungeon.game.entity.DungeonEntity;
import com.dungeon.game.entity.PlayerEntity;
import com.dungeon.game.resource.Resources;

public class FurnitureFactory {

	Sound soundFurnitureBreak = Resources.sounds.get("audio/sound/break_wood.ogg");

	public Entity doorHorizontal(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			private boolean opened = false;
			@Override public void onSignal(Entity emitter) {
				if (!opened) {
					canBlock = false;
					canBeHit = false;
					opened = true;
					Engine.entities.add(new DungeonEntity(Resources.prototypes.get("door_horizontal_open"), origin));
				}
			}
		};
	}

	public Entity doorVertical(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			private boolean opened = false;
			@Override public void onSignal(Entity emitter) {
				setAnimation(Resources.animations.get("door_vertical_open"), Engine.time());
				if (!opened) {
					canBlock = false;
					canBeHit = false;
					opened = true;
				}
			}
		};
	}

	public Entity coin(Vector2 origin, EntityPrototype prototype) {
		return new DungeonEntity(prototype, origin) {
			@Override public boolean onEntityCollision(Entity entity) {
				if (!expired && entity instanceof PlayerEntity) {
					PlayerEntity character = (PlayerEntity) entity;
					character.getPlayer().getConsole().log("Picked up gold!", Color.GOLD);
					Vector2 origin = getOrigin().cpy().add(0, getZPos());
					Vector2 destination = character.getPlayer().getRenderer().getHud().getHudWidget(character.getPlayer()).getCoinCenter();
					Bezier<Vector2> path = character.getPlayer().getRenderer().getHud().randQuadratic(origin, destination);

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
								character.getPlayer().getRenderer().getHud().addParticle(spark);
							});
						}
						@Override public void update() {
							sparkGenerator.doAtInterval();
						}
						@Override public void expire(){
							character.getPlayer().addGold(1);
						}
					};
					character.getPlayer().getRenderer().getHud().addParticle(particle);
					expire();
					return true;
				}
				return false;
			}
		};
	}

}

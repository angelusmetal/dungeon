package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.ui.particle.LinearParticle;
import com.dungeon.engine.ui.particle.PathParticle;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.automation.TimeGradient;
import com.dungeon.game.entity.PlayerEntity;

import static com.dungeon.engine.util.Util.randVect;

public class ParticleBuilder {

	private Entity emitter;
	private PlayerEntity character;
	private Vector2 destination;
	private Animation<Material> animation;
	private Color sparkColor = Color.WHITE;
	private float duration = 1f;
	private float sparkDuration = 0.5f;
	private float sparksPerSecond = 0f;
	private float sparkAttenuation = 1f;
	private Runnable endAction;

	public static ParticleBuilder of(Entity emitter, PlayerEntity character, Vector2 destination) {
		return new ParticleBuilder(emitter, character, destination);
	}

	private ParticleBuilder(Entity emitter, PlayerEntity character, Vector2 destination) {
		this.emitter = emitter;
		this.character = character;
		this.destination = destination;
	}

	public ParticleBuilder animation(Animation<Material> animation) {
		this.animation = animation;
		return this;
	}

	public ParticleBuilder sparkColor(Color sparkColor) {
		this.sparkColor = sparkColor;
		return this;
	}

	public ParticleBuilder duration(float duration) {
		this.duration = duration;
		return this;
	}

	public ParticleBuilder sparkDuration(float sparkDuration) {
		this.sparkDuration = sparkDuration;
		return this;
	}

	public ParticleBuilder sparksPerSecond(float sparksPerSecond) {
		this.sparksPerSecond = sparksPerSecond;
		return this;
	}

	public ParticleBuilder sparkAttenuation(float sparkAttenuation) {
		this.sparkAttenuation = sparkAttenuation;
		return this;
	}

	public ParticleBuilder endAction(Runnable endAction) {
		this.endAction = endAction;
		return this;
	}

	public ParticleBuilder add() {
		Vector2 origin = emitter.getOrigin().cpy().add(0, emitter.getZPos());
		Bezier<Vector2> path = character.getPlayer().getRenderer().getHud().randQuadratic(origin, destination);

		PathParticle particle;
		if (sparksPerSecond > 0) {
			particle = new PathParticle(path, animation, this.duration) {
				Metronome sparkGenerator;
				{
					// Set the color in the particle
					getColor().set(sparkColor);
					sparkGenerator = new Metronome(1f / sparksPerSecond, () -> {
						LinearParticle spark = new LinearParticle(this.origin, randVect(10, 30), this.animation, sparkDuration) {
							{
								// Set the color in each spark
								getColor().set(sparkColor);
							}
							TimeGradient gradient = TimeGradient.fadeOut(Engine.time(), duration);
							@Override public void update() {
								getColor().a = gradient.get() * sparkAttenuation;
							}
						};
						character.getPlayer().getRenderer().getHud().addParticle(spark);
					});
				}
				@Override public void update() {
					sparkGenerator.doAtInterval();
				}
				@Override public void expire(){
					endAction.run();
				}
			};
		} else {
			// No sparks
			particle = new PathParticle(path, animation, this.duration) {
				@Override public void expire(){
					endAction.run();
				}
			};
		}
		character.getPlayer().getRenderer().getHud().addParticle(particle);
		return this;
	}

}

package com.dungeon.engine.entity;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Metronome;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.TimeGradient;
import com.dungeon.engine.util.Util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.dungeon.engine.util.Util.clamp;
import static com.dungeon.engine.util.Util.length2;
import static java.lang.Math.abs;

// TODO Move this to game package
public class Traits {

    public static final float SPIN = (float) (Math.PI * 2d);
    /** Oscillate horizontally */
    static public <T extends Entity> TraitSupplier<T> hOscillate(float frequency, float amplitude) {
        return e -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(SPIN);
            return entity -> entity.getBody().move(new Vector2(MathUtils.sin((Engine.time() + phase) * SPIN * frequency) * amplitude * Engine.frameTime(), 0f));
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> TraitSupplier<T> vOscillate(float frequency, float amplitude) {
        return e -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(SPIN);
            return entity -> entity.impulse(0, MathUtils.sin((Engine.time() + phase) * SPIN * frequency) * amplitude * Engine.frameTime());
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> TraitSupplier<T> zOscillate(float frequency, float amplitude) {
        return e -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(SPIN);
            return entity -> entity.z += MathUtils.sin((Engine.time() + phase) * SPIN * frequency) * amplitude * Engine.frameTime();
        };
    }

    /** Oscillate sideways relative to the movement vector */
    static public <T extends Entity> TraitSupplier<T> movOscillate(float frequency, float amplitude) {
        float sign = amplitude > 0 ? 1 : -1;
		return e -> {
			// Randomize phase so each particle oscillates differently
			float start = Engine.time() + (float) Math.PI;//Rand.nextFloat(SPIN);
			return entity -> {
				float o = MathUtils.sin((Engine.time() - start) * SPIN * frequency) * sign;
				Vector2 impulse = entity.getMovement().cpy().rotate90(o > 0 ? 1:-1).setLength(o * amplitude);
				entity.impulse(impulse);
				// TODO Can we use impulse instead of body.move()?
//				entity.getBody().move(impulse);
			};
		};
 	}

    /** Accelerate/decelerate particle in its current direction */
    static public <T extends Entity> TraitSupplier<T> accel(float acceleration) {
        return e -> entity -> entity.speed += acceleration * Engine.frameTime();
    }

    /** Accelerate/decelerate particle vertically */
    static public <T extends Entity> TraitSupplier<T> zAccel(float acceleration) {
        return e -> entity -> entity.zSpeed += acceleration * Engine.frameTime();
    }

    static public <T extends Entity> TraitSupplier<T> autoSeek(float strength, float range, Supplier<Stream<Entity>> targetSupplier) {
        return e -> {
            // TODO Factor time in the calculation, so it can be done at fixed intervals, instead of at every frame (which also changes behavior based on framerate)
//            float interval = 0.2f;
            float range2 = length2(range);
            float seekClamp = Util.clamp(strength) * e.speed;
//            Timer targetingTimer = new Timer (interval);
            return source -> {
                // Re-target periodically
//                targetingTimer.doAtInterval(() -> {
                    ClosestEntity closest = targetSupplier.get().collect(() -> ClosestEntity.to(source), ClosestEntity::accept, ClosestEntity::combine);
                    if (closest.getDst2() < range2) {
                        Vector2 seek = closest.getEntity().getOrigin().cpy().sub(source.getOrigin());
                        seek.setLength(seekClamp);
                        source.impulse(seek);
                        source.getMovement().setLength(source.speed);
                    }
//                });
            };
        };
    }

    /** Fade in particle */
    static public <T extends Entity> TraitSupplier<T> fadeIn(float alpha, float duration) {
        return e -> {
            TimeGradient gradient = TimeGradient.fadeIn(e.getStartTime(), duration);
            return entity -> entity.color.a = gradient.get() * alpha;
        };
    }
    /** Fade in particle */
    static public <T extends Entity> TraitSupplier<T> fadeIn(float alpha) {
        return e -> {
            TimeGradient gradient = TimeGradient.fadeIn(e.getStartTime(), e.getExpirationTime() - e.getStartTime());
            return entity -> entity.color.a = gradient.get() * alpha;
        };
    }
    /** Fade out particle */
    static public <T extends Entity> TraitSupplier<T> fadeOut(float alpha) {
        return e -> {
            TimeGradient gradient = TimeGradient.fadeOut(e.getStartTime(), e.getExpirationTime() - e.getStartTime());
            return entity -> entity.color.a = gradient.get() * alpha;
        };
    }


    public static <T extends Entity> TraitSupplier<T> fadeOutLight() {
        return e -> {
            TimeGradient gradient = TimeGradient.fadeOut(e.getStartTime(), e.getExpirationTime() - e.getStartTime());
            return entity -> entity.light.dim = gradient.get();
        };
    }

    public static <T extends Entity> TraitSupplier<T> fadeOutFlare() {
        return e -> {
            TimeGradient gradient = TimeGradient.fadeOut(e.getStartTime(), e.getExpirationTime() - e.getStartTime());
            return entity -> entity.flare.dim = gradient.get();
        };
    }

    public static <T extends Entity> TraitSupplier<T> expireByTime() {
        return e -> entity -> {
            if (Engine.time() > entity.expirationTime) {
				entity.expire();
			}
		};
    }

    /** Sets animation based on a vector, using one sprite for up, down and sides (mirrored) */
    static public <T extends Entity> TraitSupplier<T> animationByVector(Function<T, Vector2> vectorProvider, Animation<Material> side, Animation<Material> up, Animation<Material> down) {
        return e -> entity -> {
            Animation<Material> newAnimation;
            Vector2 vector = vectorProvider.apply(entity);
            // Updates current animation based on the self impulse vector
            entity.getDrawScale().x = abs(entity.getDrawScale().x) * (vector.x < 0 ? -1 : 1);
            if (abs(vector.x) > abs(vector.y)) {
                // Sideways animation
                newAnimation = side;
            } else {
                // North / south animation
                newAnimation = vector.y < 0 ? down : up;
            }
            entity.updateAnimation(newAnimation);
        };
    }

    /** Inverts the horizontal draw scale based on the movement vector */
    static public <T extends Entity> TraitSupplier<T> xInvertByVector(Function<Entity, Vector2> vFunction) {
        return e -> entity -> {
            if (vFunction.apply(e).x != 0) {
                entity.getDrawScale().x = abs(entity.getDrawScale().x) * (vFunction.apply(e).x < 0 ? -1 : 1);
            }
        };
    }

    static public <T extends Entity> TraitSupplier<T> generator(float frequency, Function<T, Entity> entityProvider) {
        return e -> {
            Metronome metronome = new Metronome(frequency, () -> Engine.entities.add(entityProvider.apply(e)));
            return entity -> metronome.doAtInterval();
        };
    }

    static public <T extends Entity> TraitSupplier<T> deathClone(float timeToLive) {
        return e -> entity -> {
            Entity clone = new Entity(entity);
            clone.isStatic = true;
            clone.canBlock = false;
            clone.expirationTime = Engine.time() + timeToLive;
            clone.traits.add(expireByTime().get(clone));
            clone.traits.add(fadeOut(1f).get(clone));
            Engine.entities.add(clone);
        };
    }

    static public <T extends Entity> TraitSupplier<T> rotateFixed(float speed) {
        return e -> entity -> entity.setRotation(Engine.time() * speed);
    }

    static public <T extends Entity> TraitSupplier<T> rotateRandom(Supplier<Integer> speed) {
        return e -> {
            float actualSpeed = speed.get();
            return entity -> entity.setRotation(Engine.time() * actualSpeed);
        };
    }

    static public <T extends Entity> TraitSupplier<T> rotateVector(Vector2 rotateVector) {
        return e -> entity -> entity.setRotation(rotateVector.angle());
    }

    static public <T extends Entity> TraitSupplier<T> playSound(Sound sound, float volume, float pitchVariance, float zspeedAttn) {
        if (pitchVariance == 0) {
            return e -> entity -> Engine.audio.playSound(sound, entity.getOrigin(), volume, pitchVariance);
        } else {
            return e -> entity -> Engine.audio.playSound(sound, entity.getOrigin(), volume * clamp(abs(entity.zSpeed) / zspeedAttn), pitchVariance);
        }
    }

    static public <T extends Entity> TraitSupplier<T> playSound(List<Sound> sounds, float volume, float pitchVariance, float zspeedAttn, float chance) {
        if (pitchVariance == 0) {
            return e -> entity -> {
                if (Rand.chance(chance)) {
                    Engine.audio.playSound(Rand.pick(sounds), entity.getOrigin(), volume, pitchVariance);
                }
            };
        } else {
            return e -> entity -> {
                if (Rand.chance(chance)) {
                    Engine.audio.playSound(Rand.pick(sounds), entity.getOrigin(), volume * clamp(abs(entity.zSpeed) / zspeedAttn), pitchVariance);
                }
            };
        }
    }
}

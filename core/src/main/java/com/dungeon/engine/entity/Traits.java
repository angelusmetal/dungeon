package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.util.ClosestEntity;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.game.state.GameState;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.dungeon.engine.util.Util.length2;

// TODO Move this to game package
public class Traits {

    public static final float SPIN = (float) (Math.PI * 2d);
    /** Oscillate horizontally */
    static public <T extends Entity> TraitSupplier<T> hOscillate(float frequency, float amplitude) {
        return e -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(SPIN);
            return entity -> {
                // TODO FIXME
                entity.stop();
                entity.impulse((float) Math.sin((GameState.time() + phase) * SPIN * frequency) * amplitude, 0);
            };
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> TraitSupplier<T> vOscillate(float frequency, float amplitude) {
        return e -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(SPIN);
            return entity -> entity.impulse(0, (float) Math.sin((GameState.time() + phase) * SPIN * frequency) * amplitude * GameState.frameTime());
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> TraitSupplier<T> zOscillate(float frequency, float amplitude) {
        return e -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(SPIN);
            return entity -> entity.z += (float) Math.sin((GameState.time() + phase) * SPIN * frequency) * amplitude * GameState.frameTime();
        };
    }

    /** Accelerate/decelerate particle in its current direction */
    static public <T extends Entity> TraitSupplier<T> accel(float acceleration) {
        return e -> entity -> entity.speed += acceleration * GameState.frameTime();
    }

    /** Accelerate/decelerate particle vertically */
    static public <T extends Entity> TraitSupplier<T> zAccel(float acceleration) {
        return e -> entity -> entity.zSpeed += acceleration * GameState.frameTime();
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
                    ClosestEntity closest = targetSupplier.get().collect(() -> new ClosestEntity(source), ClosestEntity::accept, ClosestEntity::combine);
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

    /** Fade out particle */
    static public <T extends Entity> TraitSupplier<T> fadeOut(float alpha) {
        return e -> {
            // Fade until the end of life
            float ttl = e.getExpirationTime() - e.getStartTime();
            return entity -> entity.color.a = (1 - (GameState.time() - entity.getStartTime()) / ttl) * alpha;
        };
    }


    public static <T extends Entity> TraitSupplier<T> fadeOutLight() {
        return e -> {
            float ttl = e.getExpirationTime() - e.getStartTime();
            return entity -> entity.light.dim = 1 - (GameState.time() - entity.getStartTime()) / ttl;
        };
    }

    public static <T extends Entity> TraitSupplier<T> expireByTime() {
        return e -> entity -> {
            if (GameState.time() > entity.expirationTime) {
				entity.expire();
			}
		};
    }

    /** Sets animation based on a vector, using one sprite for up, down and sides (mirrored) */
    static public <T extends Entity> TraitSupplier<T> animationByVector(Function<T, Vector2> vectorProvider, Animation<TextureRegion> side, Animation<TextureRegion> up, Animation<TextureRegion> down) {
        return e -> entity -> {
            Animation<TextureRegion> newAnimation;
            Vector2 vector = vectorProvider.apply(entity);
            // Updates current animation based on the self impulse vector
            entity.setInvertX(vector.x < 0);
            if (Math.abs(vector.x) > Math.abs(vector.y)) {
                // Sideways animation
                newAnimation = side;
            } else {
                // North / south animation
                newAnimation = vector.y < 0 ? down : up;
            }
            entity.updateCurrentAnimation(newAnimation);
        };
    }

    static public <T extends Entity> TraitSupplier<T> generator(float frequency, Function<T, Entity> entityProvider) {
        return e -> {
            Timer timer = new Timer(frequency);
            return entity -> timer.doAtInterval(() -> GameState.entities.add(entityProvider.apply(entity)));
        };
    }

    static public <T extends Entity> TraitSupplier<T> generatorMultiple(int minCount, int maxCount, Function<T, Entity> entityProvider) {
        return e -> entity -> {
            int count = minCount != maxCount ? Rand.between(minCount, maxCount) : minCount;
            for (int i = 0; i < count ; ++i) {
                GameState.entities.add(entityProvider.apply(entity));
            }
        };
    }

}

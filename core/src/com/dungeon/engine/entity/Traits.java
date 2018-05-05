package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.util.Rand;
import com.dungeon.game.state.GameState;

import java.util.function.Function;

public class Traits {

    /** Oscillate horizontally */
    static public <T extends Entity> TraitSupplier<T> hOscillate(float frequency, float amplitude) {
        return (e) -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(6.28f);
            return (entity) -> entity.impulse((float) Math.sin((GameState.time() + phase) * frequency) * amplitude, 0);
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> TraitSupplier<T> vOscillate(float frequency, float amplitude) {
        return (e) -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(6.28f);
            return (entity) -> entity.impulse(0, (float) Math.sin((GameState.time() + phase) * frequency) * amplitude);
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> TraitSupplier<T> zOscillate(float frequency, float amplitude) {
        return (e) -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(6.28f);
            return (entity) -> entity.z += (float) Math.sin((GameState.time() + phase) * frequency) * amplitude * GameState.frameTime();
        };
    }

    /** Accelerate/decelerate particle in its current direction */
    static public <T extends Entity> TraitSupplier<T> accel(float acceleration) {
        return (e) -> (entity) -> entity.speed += acceleration * GameState.time();
    }

    /** Accelerate/decelerate particle vertically */
    static public <T extends Entity> TraitSupplier<T> zAccel(float acceleration) {
        return (e) -> (entity) -> entity.zSpeed += acceleration * GameState.frameTime();
    }

    // NOTE Not thread-safe; use ThreadLocal if multi-thread is desired
    private static final Vector2 target = new Vector2();
    private static final Vector2 seek = new Vector2();

    static public <T extends Entity> TraitSupplier<T> autoSeek(float strength, float range, Function<Entity, Boolean> targetting) {
        return (e) -> {
            float range2 = range * range;
            return (source) -> {
                boolean found = false;
                // Find closest target within range
                for (Entity entity : GameState.getEntities()) {
                    if (targetting.apply(entity)) {
                        target.set(entity.getPos()).sub(source.getPos());
                        float len = target.len2();
                        if (len < range2 && (!found || len < seek.len2())) {
                            found = true;
                            seek.set(target);
                        }
                    }
                }
                // If a target has been found, autoseek
                if (found) {
                    float seekClamp = strength * source.speed;
                    float speedClamp = source.speed - seekClamp;
                    seek.setLength(seekClamp);
                    source.impulse(seek);
                    source.getMovement().setLength(speedClamp);
                }
            };
        };
    }

    /** Fade out particle */
    static public <T extends Entity> TraitSupplier<T> fadeOut(float alpha) {
        return (e) -> {
            // Fade until the end of life
            float ttl = e.getExpirationTime() - e.getStartTime();
            return (entity) -> entity.color.a = (1 - (GameState.time() - entity.getStartTime()) / ttl) * alpha;
        };
    }


    public static <T extends Entity> TraitSupplier<T> fadeOutLight() {
        return (e) -> {
            float diameter = e.light.diameter;
            float ttl = e.getExpirationTime() - e.getStartTime();
            return (entity) -> entity.light.diameter = (1 - (GameState.time() - entity.getStartTime()) / ttl) * diameter;
        };
    }

    public static <T extends Entity> TraitSupplier<T> expireByTime() {
        return (e) -> (entity) -> {
            if (GameState.time() > entity.expirationTime) {
				entity.expire();
			}
		};
    }

    /** Sets animation based on a vector, using one sprite for up, down and sides (mirrored) */
    static public <T extends Entity> TraitSupplier<T> animationByVector(Function<T, Vector2> vectorProvider, Animation<TextureRegion> side, Animation<TextureRegion> up, Animation<TextureRegion> down) {
        return (e) -> (entity) -> {
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
            // Only set if animation changed
            if (!entity.isCurrentAnimation(newAnimation)) {
                entity.setCurrentAnimation(new GameAnimation(newAnimation, GameState.time()));
            }
        };
    }

    static public <T extends Entity> TraitSupplier<T> generator(float frequency, Function<T, Entity> entityProvider) {
        return (e) -> {
            Timer timer = new Timer(frequency);
            return (entity) -> timer.doAtInterval(() -> GameState.addEntity(entityProvider.apply(entity)));
        };
    }

}

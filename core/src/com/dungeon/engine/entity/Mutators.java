package com.dungeon.engine.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.animation.GameAnimation;
import com.dungeon.engine.random.Rand;

import java.util.function.Function;

public class Mutators {

    /** Oscillate horizontally */
    static public <T extends Entity> MutatorSupplier<T> hOscillate(float frequency, float amplitude) {
        return (e) -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(6.28f);
            return (entity, state) -> entity.impulse((float) Math.sin((state.getStateTime() + phase) * frequency) * amplitude, 0);
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> MutatorSupplier<T> vOscillate(float frequency, float amplitude) {
        return (e) -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(6.28f);
            return (entity, state) -> entity.impulse(0, (float) Math.sin((state.getStateTime() + phase) * frequency) * amplitude);
        };
    }

    /** Oscillate vertically */
    static public <T extends Entity> MutatorSupplier<T> zOscillate(float frequency, float amplitude) {
        return (e) -> {
            // Randomize phase so each particle oscillates differently
            float phase = Rand.nextFloat(6.28f);
            return (entity, state) -> entity.z += (float) Math.sin((state.getStateTime() + phase) * frequency) * amplitude * state.getFrameTime();
        };
    }

    /** Accelerate/decelerate particle in its current direction */
    static public <T extends Entity> MutatorSupplier<T> accel(float acceleration) {
        return (p) -> (particle, state) -> particle.speed += acceleration * state.getStateTime();
    }

    /** Accelerate/decelerate particle vertically */
    static public MutatorSupplier<Particle> zAccel(float acceleration) {
        return (p) -> (particle, state) -> particle.zSpeed += acceleration * state.getFrameTime();
    }

    // NOTE Not thread-safe; use ThreadLocal if multi-thread is desired
    private static final Vector2 target = new Vector2();
    private static final Vector2 seek = new Vector2();

    static public <T extends Entity> MutatorSupplier<T> autoSeek(float strength, float range, Function<Entity, Boolean> targetting) {
        return (p) -> {
            float range2 = range * range;
            return (projectile, state) -> {
                boolean found = false;
                // Find closest target within range
                for (Entity entity : state.getEntities()) {
                    if (targetting.apply(entity)) {
                        target.set(entity.getPos()).sub(projectile.getPos());
                        float len = target.len2();
                        if (len < range2 && (!found || len < seek.len2())) {
                            found = true;
                            seek.set(target);
                        }
                    }
                }
                // If a target has been found, autoseek
                if (found) {
                    float seekClamp = strength * projectile.speed;
                    float speedClamp = projectile.speed - seekClamp;
                    seek.setLength(seekClamp);
                    projectile.impulse(seek);
                    projectile.getMovement().setLength(speedClamp);
                }
            };
        };
    }

    /** Fade out particle */
    static public MutatorSupplier<Particle> fadeOut(float alpha) {
        return (p) -> {
            // Fade until the end of life
            return (particle, state) -> particle.color.a = (1 - (state.getStateTime() - particle.getStartTime()) / particle.getTimeToLive()) * alpha;
        };
    }


    public static MutatorSupplier<Particle>  fadeOutLight() {
        return (p) -> {
            float diameter = p.light.diameter;
            return (particle, state) -> particle.light.diameter = (1 - (state.getStateTime() - particle.getStartTime()) / particle.getTimeToLive()) * diameter;
        };
    }

    /** Sets animation based on a vector, using one sprite for up, down and sides (mirrored) */
    static public <T extends Entity> MutatorSupplier<T> faceSelfImpulse(Function<T, Vector2> vectorProvider, Animation<TextureRegion> side, Animation<TextureRegion> up, Animation<TextureRegion> down) {
        return (e) -> (entity, state) -> {
            Animation<TextureRegion> currentAnimation = entity.getCurrentAnimation().getAnimation();
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
            if (newAnimation != currentAnimation) {
                entity.setCurrentAnimation(new GameAnimation(newAnimation, state.getStateTime()));
            }
        };
    }

}

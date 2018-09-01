package com.dungeon.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OverlayText {

    private final String text;
    private final Vector2 origin;
    private final Color color;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final List<Runnable> traits = new ArrayList<>();
    private boolean expired;
    private int length;
    private boolean outline;

    public OverlayText(Vector2 origin, String text, Color color, BitmapFont font) {
        this.origin = origin.cpy();
        this.text = text;
        this.color = color.cpy();
        this.font = font;
        this.layout = new GlyphLayout(font, text);
        this.length = text.length();
        this.outline = true;
    }

    public OverlayText spell(float time) {
        spell(time, 0);
        return this;
    }

    public OverlayText spell(float time, float delay) {
        linear(time, delay, p -> length = (int) (p * text.length()));
        return this;
    }

    public OverlayText fadeout(float time) {
        fadeout(time, 0);
        return this;
    }

    public OverlayText fadeout(float time, float delay) {
        float startAlpha = color.a;
        linear(time, delay, p -> color.a = (1 - p) * startAlpha);
        timeToLive(time + delay);
        return this;
    }

    public OverlayText linear(float time, float delay, Consumer<Float> fader) {
        float startTime = Engine.time() + delay;
        traits.add(() -> {
            float point =  Math.min(Math.max(Engine.time() - startTime, 0) / time, 1);
            fader.accept(point);
        });
        return this;
    }

    public OverlayText move(float moveX, float moveY) {
        traits.add(() -> origin.add(moveX * Engine.frameTime(), moveY * Engine.frameTime()));
        return this;
    }

    public OverlayText timeToLive(float time) {
        float expiration = Engine.time() + time;
        traits.add(() -> {
            if (Engine.time() >= expiration) {
                expired = true;
            }
        });
        return this;
    }
    
    public OverlayText outline(boolean overlay) {
        this.outline = overlay;
        return this;
    }

    public void think() {
        traits.forEach(Runnable::run);
    }

    public boolean isExpired() {
        return expired;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public GlyphLayout getLayout() {
        return layout;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasOutline() {
        return outline;
    }

    public void draw(SpriteBatch batch, ViewPort viewPort) {
        // Only create a new layout if we're chopping the texts
        GlyphLayout local = (length < text.length())
                ? new GlyphLayout(font, text.substring(0, length))
                : layout;
        font.draw(batch, local, origin.x - viewPort.cameraX - layout.width / 2, origin.y - viewPort.cameraY - layout.height / 2);
    }
}

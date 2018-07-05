package com.dungeon.game.state;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.viewport.ViewPort;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OverlayText {

    private static final String FONT = "alegreya-sans-sc-outline-9";
    private final String text;
    private final Vector2 origin;
    private final Color color;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final List<Runnable> traits = new ArrayList<>();
    private boolean expired;
    private int length;

    public OverlayText(Vector2 origin, String text) {
        this(origin, text, Color.WHITE);
    }

    public OverlayText(Vector2 origin, String text, Color color) {
        this(origin, text, color, ResourceManager.getFont(FONT));
    }

    public OverlayText(Vector2 origin, String text, Color color, BitmapFont font) {
        this.origin = origin.cpy();
        this.text = text;
        this.color = color.cpy();
        this.font = font;
        font.setColor(this.color);
        this.layout = new GlyphLayout(font, text);
        this.length = text.length();
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
        float startTime = GameState.time() + delay;
        traits.add(() -> {
            float point =  Math.min(Math.max(GameState.time() - startTime, 0) / time, 1);
            fader.accept(point);
        });
        return this;
    }

    public OverlayText move(float moveX, float moveY) {
        traits.add(() -> origin.add(moveX * GameState.frameTime(), moveY * GameState.frameTime()));
        return this;
    }

    public OverlayText timeToLive(float time) {
        float expiration = GameState.time() + time;
        traits.add(() -> {
            if (GameState.time() >= expiration) {
                expired = true;
            }
        });
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

    public void draw(SpriteBatch batch, ViewPort viewPort) {
        font.setColor(color);
        font.draw(batch, text, origin.x - viewPort.cameraX - layout.width / 2, origin.y - viewPort.cameraY - layout.height / 2,
                0, length, 0, Align.left, false, null);
        // TODO We should be able to draw the layout directly, to improve performance, but I'm unable to update the color of the layout
//        font.draw(batch, layout, origin.x - viewPort.cameraX - layout.width / 2, origin.y - viewPort.cameraY - layout.height / 2);
    }
}

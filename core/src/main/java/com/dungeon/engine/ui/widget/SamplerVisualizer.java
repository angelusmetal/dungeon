package com.dungeon.engine.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.dungeon.engine.util.CyclicSampler;
import com.dungeon.engine.util.Util;

import java.util.function.Function;

public class SamplerVisualizer extends AbstractWidget implements Widget, Disposable {

	private final CyclicSampler sampler;
	private final String label;
	private final Pixmap pixmap;
	private final Texture texture;
	private final Color color = Color.RED.cpy();
	private final Color fontColor = new Color(1f, 1f, 1f, 0.5f);
	private BitmapFont font = new BitmapFont();
	private Function<Double, String> formatter = Util::nanosToString;

	public SamplerVisualizer(CyclicSampler sampler, String label) {
		this.sampler = sampler;
		this.label = label;
		this.width = sampler.getSize();
		this.height = 50;
		this.pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		this.texture = new Texture(pixmap);
	}

	public SamplerVisualizer font(BitmapFont font) {
		this.font = font;
		return this;
	}

	public SamplerVisualizer color(Color color) {
		this.color.set(color);
		return this;
	}

	public SamplerVisualizer formatter(Function<Double, String> formatter) {
		this.formatter = formatter;
		return this;
	}

	@Override
	public void draw(SpriteBatch batch) {
		double max = Math.max(sampler.maxValue(), height);
		pixmap.setColor(0, 0, 0, 0);
		pixmap.fill();
		pixmap.setColor(Color.WHITE);
		sampler.forEach((s, v) -> pixmap.drawLine(s, height, s, (int) (height - (v * height / max))));
		texture.draw(pixmap, 0, 0);
		batch.setColor(color);
		batch.draw(texture, x, y);
		font.setColor(fontColor);
		font.draw(batch, label + " " + formatter.apply(sampler.currentValue()), x, y + height);
	}

	@Override
	public void dispose() {
		texture.dispose();
		pixmap.dispose();
	}

}

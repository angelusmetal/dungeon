package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.ui.widget.AbstractWidget;
import com.dungeon.engine.ui.widget.Widget;
import com.dungeon.engine.util.Util;
import com.dungeon.game.player.Player;

public class HeartWidget extends AbstractWidget implements Widget {

	private final float healthPerContainer;
	private final Animation<Material> hearts;
	private final TextureRegion energy;
	private final Player player;
	private final int heartTab;

	public HeartWidget(Player player) {
		this(player, 50f);
	}

	public HeartWidget(Player player, float healthPerContainer) {
		this.healthPerContainer = healthPerContainer;
		hearts = Resources.animations.get("heart_container");
		energy = Resources.animations.get("pixel").getKeyFrame(0f).getDiffuse();
		this.player = player;
		heartTab = hearts.getKeyFrame(0).getDiffuse().getRegionWidth() + 2;
		height = hearts.getKeyFrame(0).getDiffuse().getRegionHeight();
	}

	@Override public void draw(SpriteBatch batch) {
		int containers = (int) Math.ceil(player.getAvatar().getMaxHealth() / healthPerContainer);
		float energyFill = player.getAvatar().getEnergy() / player.getAvatar().getMaxEnergy();
		int newWidth = hearts.getKeyFrame(0).getDiffuse().getRegionWidth() * containers + 2 * (containers - 1);
		if (newWidth != width) {
			width = newWidth;
			sizeObserver.run();
		}
		float remaining = player.getAvatar().getHealth();
		for (int i = 0; i < containers; ++i) {
			TextureRegion heart = hearts.getKeyFrame(Util.clamp(remaining / healthPerContainer)).getDiffuse();
			batch.draw(heart, x + i * heartTab, y);
			remaining -= healthPerContainer;
		}
		batch.setColor(Color.BLUE);
		batch.draw(energy, x, y - 4, width * energyFill, 2);
		batch.setColor(Color.WHITE);
	}
}

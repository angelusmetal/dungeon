package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.ui.AbstractWidget;
import com.dungeon.engine.ui.Widget;
import com.dungeon.engine.util.Util;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.Resources;

public class HeartWidget extends AbstractWidget implements Widget {

	private final float healthPerContainer;
	private final Animation<TextureRegion> hearts;
	private final Player player;
	private final int heartTab;

	public HeartWidget(Player player) {
		this(player, 50f);
	}

	public HeartWidget(Player player, float healthPerContainer) {
		this.healthPerContainer = healthPerContainer;
		hearts = Resources.animations.get("heart_container");
		this.player = player;
		heartTab = hearts.getKeyFrame(0).getRegionWidth() + 2;
		height = hearts.getKeyFrame(0).getRegionHeight();
	}

	@Override public void draw(SpriteBatch batch) {
		int containers = (int) Math.ceil(player.getAvatar().getMaxHealth() / healthPerContainer);
		int newWidth = hearts.getKeyFrame(0).getRegionWidth() * containers + 2 * (containers - 1);
		if (newWidth != width) {
			width = newWidth;
			sizeObserver.run();
		}
		float remaining = player.getAvatar().getHealth();
		for (int i = 0; i < containers; ++i) {
			TextureRegion heart = hearts.getKeyFrame(Util.clamp(remaining / healthPerContainer));
			batch.draw(heart, x + i * heartTab, y - height);
			remaining -= healthPerContainer;
		}
	}
}

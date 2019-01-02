package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.ui.widget.AbstractWidget;
import com.dungeon.engine.ui.widget.Widget;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.Resources;

public class CoinsWidget extends AbstractWidget implements Widget {

	private final Animation<TextureRegion> coins;
	private final BitmapFont font;
	private final Player player;
	private final int spriteHeight;

	public CoinsWidget(Player player) {
		coins = Resources.animations.get("coin");
		font = Resources.fonts.get(Resources.DEFAULT_FONT);
		this.player = player;
		spriteHeight = coins.getKeyFrame(0).getRegionHeight();
		height = spriteHeight + (int) font.getLineHeight();
		width = coins.getKeyFrame(0).getRegionWidth();
	}

	@Override public void draw(SpriteBatch batch) {
		batch.draw(coins.getKeyFrame(Engine.time()), x, y + font.getLineHeight());
		font.draw(batch, Integer.toString(player.getGold()), x, y + font.getLineHeight());
	}

	@Override public Vector2 getCenter() {
		return new Vector2(x + width / 2f, y + height - spriteHeight / 2f);
	}
}

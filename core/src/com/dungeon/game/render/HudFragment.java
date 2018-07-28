package com.dungeon.game.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.state.GameState;
import com.dungeon.game.state.OverlayText;

public class HudFragment implements RenderFragment {

	private static float HEALTH_PER_HEART = 50f;
	private static int MARGIN = 4;
	private static int HEART_TAB = 12 + 2;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Player player;
	private boolean enabled = true;
	private final BitmapFont font;
	private final Animation<TextureRegion> hearts;
	private final Animation<TextureRegion> coins;


	public HudFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer, Player player) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.player = player;
		font = ResourceManager.getFont(OverlayText.DEFAULT_FONT);
		hearts = ResourceManager.getAnimation("heart_container");
		coins = ResourceManager.getAnimation("coin");
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				int x = MARGIN;
				int y = viewPort.cameraHeight - MARGIN;
				// Display coins
				TextureRegion coin = coins.getKeyFrame(GameState.time());
				y -= coin.getRegionHeight();
				batch.draw(coin, x, y);
				font.draw(batch, Integer.toString(player.getGold()), x, y);
				x += 12;
				// Display weapon
				y = viewPort.cameraHeight - MARGIN;
				if (player.getWeapon().getAnimation() != null) {
					TextureRegion weapon = player.getWeapon().getAnimation().getKeyFrame(GameState.time());
					y -= weapon.getRegionHeight();
					batch.draw(weapon, x, y);
					x += weapon.getRegionWidth();
				}
				// Display heart containers
				y = viewPort.cameraHeight - MARGIN - HEART_TAB;
				int containers = (int) Math.ceil(player.getAvatar().getMaxHealth() / HEALTH_PER_HEART);
				float remaining = player.getAvatar().getHealth();
				for (int i = 0; i < containers; ++i) {
					TextureRegion heart = hearts.getKeyFrame(Util.clamp(remaining / HEALTH_PER_HEART));
					batch.draw(heart, x + i * HEART_TAB, y);
					remaining -= HEALTH_PER_HEART;
				}
			});
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {}

}

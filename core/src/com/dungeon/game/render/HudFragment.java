package com.dungeon.game.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.state.GameState;

public class HudFragment implements RenderFragment {

	private static float HEALTH_PER_HEART = 50f;
	private static int MARGIN = 4;
	private static int HEART_TAB = 12 + 2;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final Player player;
	private boolean enabled = true;
	private final Animation<TextureRegion> frames;

	public HudFragment(ViewPort viewPort, ViewPortBuffer viewportBuffer, Player player) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.player = player;
		frames = ResourceManager.getAnimation("heart_container");
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render((batch) -> {
				// Display heart containers
				int containers = (int) Math.ceil(player.getAvatar().getMaxHealth() / HEALTH_PER_HEART);
				float remaining = player.getAvatar().getHealth();
				int x = viewPort.cameraWidth - MARGIN - (HEART_TAB * containers);
				for (int i = 0; i < containers; ++i) {
					TextureRegion heart = frames.getKeyFrame(Util.clamp(remaining / HEALTH_PER_HEART));
					batch.draw(heart, x + i * HEART_TAB, MARGIN);
					remaining -= HEALTH_PER_HEART;
				}
				// Display weapon
				if (player.getWeapon().getAnimation() != null) {
					TextureRegion weapon = player.getWeapon().getAnimation().getKeyFrame(GameState.time());
					batch.draw(weapon, viewPort.cameraWidth - MARGIN - weapon.getRegionWidth(), MARGIN + HEART_TAB);
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

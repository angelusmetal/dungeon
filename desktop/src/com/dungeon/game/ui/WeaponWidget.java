package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.Engine;
import com.dungeon.engine.ui.widget.AbstractWidget;
import com.dungeon.engine.ui.widget.Widget;
import com.dungeon.game.player.Player;

public class WeaponWidget extends AbstractWidget implements Widget {

	private Player player;

	public WeaponWidget(Player player) {
		this.player = player;
	}

	@Override public void draw(SpriteBatch batch) {
		if (player.getWeapon().getHudAnimation() != null) {
			TextureRegion weapon = player.getWeapon().getHudAnimation().getKeyFrame(Engine.time()).getDiffuse();
			if (width != weapon.getRegionWidth() || height != weapon.getRegionHeight()) {
				width = weapon.getRegionWidth();
				height = weapon.getRegionHeight();
				sizeObserver.run();
			}
			batch.draw(weapon, x, y);
		}
	}
}

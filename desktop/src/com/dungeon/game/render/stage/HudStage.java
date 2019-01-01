package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.ui.HLayout;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.ui.CoinsWidget;
import com.dungeon.game.ui.HeartWidget;
import com.dungeon.game.ui.WeaponWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HudStage implements RenderStage {

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private boolean enabled = true;
	private final HLayout layout = new HLayout();
	private final CoinsWidget coinsWidget;
	private final WeaponWidget weaponWidget;
	private final HeartWidget heartWidget;

	private List<HudParticle> particles = new ArrayList<>();

	public HudStage(ViewPort viewPort, ViewPortBuffer viewportBuffer, Player player) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		coinsWidget = new CoinsWidget(player);
		weaponWidget = new WeaponWidget(player);
		heartWidget = new HeartWidget(player);

		layout.pad(4);
		layout.setX(4);
		layout.setY(viewPort.cameraHeight - 4);
		layout.add(coinsWidget);
		layout.add(weaponWidget);
		layout.add(heartWidget);
	}

	public void addParticle(HudParticle particle) {
		particles.add(particle);
	}

	public Bezier<Vector2> randQuadratic(Vector2 origin, Vector2 destination) {
		Vector2 rand = new Vector2(Rand.between(50, 150), 0).rotate(Rand.between(90, 270));
		return new Bezier<>(
				viewPort.worldToScreen(origin),
				viewPort.worldToScreen(rand.add(origin)),
				destination);
	}

	@Override
	public void render() {
		if (enabled) {
			viewportBuffer.render(batch -> {
				layout.draw(batch);
				// Update & draw particles
				for (Iterator<HudParticle> p = particles.iterator(); p.hasNext();) {
					HudParticle particle = p.next();
					particle.drawAndUpdate(batch);
					if (particle.isExpired()) {
						particle.runAction();
						p.remove();
					}
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

	public CoinsWidget getCoinsWidget() {
		return coinsWidget;
	}

	public WeaponWidget getWeaponWidget() {
		return weaponWidget;
	}

	public HeartWidget getHeartWidget() {
		return heartWidget;
	}
}

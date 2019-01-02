package com.dungeon.game.render.stage;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.ui.particle.Particle;
import com.dungeon.engine.ui.particle.PathParticle;
import com.dungeon.engine.ui.widget.HLayout;
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

	private List<Particle> particles = new ArrayList<>();
	private List<Particle> newParticles = new ArrayList<>();

	public HudStage(ViewPort viewPort, ViewPortBuffer viewportBuffer, Player player) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		coinsWidget = new CoinsWidget(player);
		weaponWidget = new WeaponWidget(player);
		heartWidget = new HeartWidget(player);

		layout.pad(4);
		layout.align(HLayout.Alignment.TOP);
		layout.setX(4);
		layout.add(coinsWidget);
		layout.add(weaponWidget);
		layout.add(heartWidget);
		layout.setY(viewPort.cameraHeight - layout.getHeight() - 4);
	}

	public void addParticle(Particle particle) {
		newParticles.add(particle);
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
			particles.addAll(newParticles);
			newParticles.clear();
			viewportBuffer.render(batch -> {
				layout.draw(batch);
				// Update & draw particles
				for (Iterator<Particle> p = particles.iterator(); p.hasNext();) {
					Particle particle = p.next();
					particle.drawAndUpdate(batch);
					if (particle.isExpired()) {
						particle.expire();
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

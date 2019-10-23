package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.repository.Repository;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HudStage implements RenderStage {

	public static final float SCALE = 3f;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final SpriteBatch batch;
	private boolean enabled = true;
	private final HLayout layout = new HLayout();
	private final Map<Player, Hud> hudByPlayer = new HashMap<>();
	private final Repository<Particle> particles = new Repository<>();

	private static class Hud {
		private final CoinsWidget coinsWidget;
		private final WeaponWidget weaponWidget;
		private final HeartWidget heartWidget;

		public Hud(Player player) {
			coinsWidget = new CoinsWidget(player);
			weaponWidget = new WeaponWidget(player);
			heartWidget = new HeartWidget(player);
		}
	}

	public HudStage(ViewPort viewPort, ViewPortBuffer viewportBuffer, SpriteBatch batch, List<Player> players) {
		this.viewPort = viewPort;
		this.viewportBuffer = viewportBuffer;
		this.batch = batch;

		layout.pad(4);
		layout.align(HLayout.Alignment.TOP);
		layout.setX(4);
		players.forEach(player -> {
			Hud hud = new Hud(player);
			layout.add(hud.coinsWidget);
			layout.add(hud.weaponWidget);
			layout.add(hud.heartWidget);
			hudByPlayer.put(player, hud);
		});
		layout.setY(viewPort.cameraHeight - layout.getHeight() - 4);
	}

	public void addParticle(Particle particle) {
		particles.add(particle);
	}

	public Bezier<Vector2> randQuadratic(Vector2 origin, Vector2 destination) {
		Vector2 rand = new Vector2(Rand.between(50, 150), 0).rotate(Rand.between(90, 270));
		return new Bezier<>(
				viewPort.worldToScreen(origin).scl(viewPort.getScale() / SCALE),
				viewPort.worldToScreen(rand.add(origin)).scl(viewPort.getScale() / SCALE),
				destination);
	}

	@Override
	public void render() {
		if (enabled) {
			batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width / SCALE, viewPort.height / SCALE);
			batch.begin();
			layout.draw(batch);
			particles.update(p -> p.drawAndUpdate(batch), Particle::isExpired, Particle::expire);
			batch.end();
			batch.setColor(Color.WHITE);
		}
	}

	@Override
	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public void dispose() {}

	public CoinsWidget getCoinsWidget(Player player) {
		return hudByPlayer.get(player).coinsWidget;
	}

	public WeaponWidget getWeaponWidget(Player player) {
		return hudByPlayer.get(player).weaponWidget;
	}

	public HeartWidget getHeartWidget(Player player) {
		return hudByPlayer.get(player).heartWidget;
	}
}

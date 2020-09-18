package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.entity.repository.Repository;
import com.dungeon.engine.render.Renderer;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.ui.particle.Particle;
import com.dungeon.engine.ui.widget.HLayout;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.ui.CharacterHudWidget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HudStage implements Renderer {

	public static final float SCALE = 2f;

	private final ViewPort viewPort;
	private final ViewPortBuffer viewportBuffer;
	private final SpriteBatch batch;
	private final HLayout layout = new HLayout();
	private final Map<Player, Hud> hudByPlayer = new HashMap<>();
	private final Repository<Particle> particles = new Repository<>();

	private static class Hud {
		private final CharacterHudWidget characterHudWidget;

		public Hud(Player player, ViewPort viewPort) {
			characterHudWidget = new CharacterHudWidget(player, viewPort);
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
			Hud hud = new Hud(player, viewPort);
			layout.add(hud.characterHudWidget);
			hudByPlayer.put(player, hud);
		});
		layout.setY((int) (viewPort.height / SCALE) - layout.getHeight() - 4);
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
		batch.getProjectionMatrix().setToOrtho2D(0, 0, viewPort.width / SCALE, viewPort.height / SCALE);
		batch.begin();
		layout.draw(batch);
		particles.update(p -> p.drawAndUpdate(batch), Particle::isExpired, Particle::expire);
		batch.end();
	}

	@Override
	public void dispose() {}

	public CharacterHudWidget getHudWidget(Player player) {
		return hudByPlayer.get(player).characterHudWidget;
	}
}

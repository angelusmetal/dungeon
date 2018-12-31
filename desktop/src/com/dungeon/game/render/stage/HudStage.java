package com.dungeon.game.render.stage;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.entity.Entity;
import com.dungeon.engine.render.ViewPortBuffer;
import com.dungeon.engine.ui.HLayout;
import com.dungeon.engine.util.Rand;
import com.dungeon.engine.util.Util;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.Resources;
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

	public static class HudParticle {
		Vector2 origin = new Vector2();
		Animation<TextureRegion> animation;
		boolean expired;
		Bezier<Vector2> path;
		float startTime;
		float duration;

		public HudParticle(Bezier<Vector2> path, float duration, Animation<TextureRegion> animation) {
			this.path = path;
			this.animation = animation;
			this.startTime = Engine.time();
			this.duration = duration;
		}

		public void drawAndUpdate(SpriteBatch batch) {
			if (Engine.time() >= startTime + duration) {
				expired = true;
			} else {
				path.valueAt(origin, (Engine.time() - startTime) / duration);
				TextureRegion frame = animation.getKeyFrame(Engine.time());
				batch.draw(frame, origin.x - frame.getRegionWidth() / 2f, origin.y - frame.getRegionHeight() / 2f);
			}
		}
	}

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

	public void addParticle(Bezier<Vector2> path, float duration, Animation<TextureRegion> animation) {
		particles.add(new HudParticle(path, duration, animation));
	}

	public void addRandomQuadraticParticle(Vector2 origin, Vector2 destination, Animation<TextureRegion> animation) {
		Vector2 rand = new Vector2(Rand.between(50, 100), 0).rotate(Rand.between(0, 360));
		HudParticle particle = new HudParticle(new Bezier<>(
				viewPort.worldToScreen(origin),
				viewPort.worldToScreen(rand.add(origin)),
				destination), 1f, animation);
		particles.add(particle);
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
					if (particle.expired) {
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

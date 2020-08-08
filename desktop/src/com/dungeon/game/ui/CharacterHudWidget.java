package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.resource.Resources;
import com.dungeon.engine.ui.widget.AbstractWidget;
import com.dungeon.engine.ui.widget.Widget;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.DungeonResources;

public class CharacterHudWidget extends AbstractWidget implements Widget {
	private final Animation<Sprite> coins;
	private final Animation<Sprite> face;
	private final Animation<Sprite> bars;
	private final Animation<Sprite> nameplate;
	private final Animation<Sprite> health;
	private final Animation<Sprite> energy;
	private final Animation<Sprite> experience;
	private final BitmapFont font;
	private final BitmapFont chubbyFont;
	private final Player player;
	private final ShaderProgram shaderOutline;
	private final Color outlineColor;
	private final ViewPort viewPort;

	private final float healthWidth;
	private final float energyWidth;
	private final float experienceWidth;

	public CharacterHudWidget(Player player, ViewPort viewPort) {
		face = getFace(player);
		coins = Resources.animations.get("coin");
 		bars = Resources.animations.get("hud_bars");
		nameplate = Resources.animations.get("hud_nameplate");
		health = Resources.animations.get("hud_health");
		energy = Resources.animations.get("hud_energy");
		experience = Resources.animations.get("hud_experience");
		font = Resources.fonts.get(DungeonResources.DEFAULT_FONT);
		chubbyFont = Resources.fonts.get("chubby-9");
		this.player = player;
		shaderOutline = Resources.shaders.get("df_vertex.glsl|outline_border_fragment.glsl");
		height = 48;
		width = 144;
		this.viewPort = viewPort;
		outlineColor = new Color(0x504057ff);
		healthWidth = health.getKeyFrame(0).getWidth();
		energyWidth = energy.getKeyFrame(0).getWidth();
		experienceWidth = experience.getKeyFrame(0).getWidth();
	}

	private Animation<Sprite> getFace(Player player) {
		if (player.getCharacterId() == 0) {
			return Resources.animations.get("hud_kara");
		} else if (player.getCharacterId() == 1) {
			return Resources.animations.get("hud_jack");
		} else if (player.getCharacterId() == 2) {
			return Resources.animations.get("hud_mort");
		} else if (player.getCharacterId() == 3) {
			return Resources.animations.get("hud_alma");
		} else {
			return Resources.animations.get("hud_alma");
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		Sprite frame = nameplate.getKeyFrame(Engine.time());
		frame.setColor(player.getColor());
		frame.setPosition(x + 23, y + 28);
		frame.draw(batch);

		frame = bars.getKeyFrame(Engine.time());
		frame.setPosition(x + 47, y + 11);
		frame.draw(batch);

		// Display health
		float healthFill = player.getAvatar().getHealth() / player.getAvatar().getMaxHealth();
		frame = new Sprite(health.getKeyFrame(Engine.time()));
		frame.setRegionX((int) (frame.getRegionX() + frame.getRegionWidth() * (1f - healthFill)));
		frame.setSize((int) (frame.getWidth() * healthFill), frame.getHeight());
		frame.setPosition(x + 47, y + 22);
		frame.draw(batch);

		// Display energy
		float energyFill = player.getAvatar().getEnergy() / player.getAvatar().getMaxEnergy();
		frame = new Sprite(energy.getKeyFrame(Engine.time()));
		frame.setRegionX((int) (frame.getRegionX() + frame.getRegionWidth() * (1f - energyFill)));
		frame.setSize((int) (frame.getWidth() * energyFill), frame.getHeight());
		frame.setPosition(x + 47, y + 16);
		frame.draw(batch);

		// Display experience
		frame = experience.getKeyFrame(Engine.time());
		frame.setPosition(x + 47, y + 11);
		frame.draw(batch);

		// Display mug
		frame = face.getKeyFrame(Engine.time());
		frame.setPosition(x, y);
		frame.draw(batch);

		// Display coins
		frame = coins.getKeyFrame(Engine.time());
		frame.setPosition(x + 50, y);
		frame.draw(batch);
		font.draw(batch, Integer.toString(player.getGold()), x + 59, y + font.getLineHeight());

		// Display weapon
		if (player.getWeapon().getAnimation() != null) {
			TextureRegion weapon = player.getWeapon().getAnimation().getKeyFrame(Engine.time());
			batch.draw(weapon, x + 120, y + 8);
		}

		batch.end();
		shaderOutline.begin();
		shaderOutline.setUniformf("u_color", outlineColor);
		shaderOutline.setUniformf("u_viewportInverse", new Vector2(1f / viewPort.width, 1f / viewPort.height));
		shaderOutline.end();
		batch.setShader(shaderOutline);
		batch.begin();
		chubbyFont.draw(batch, player.getName() + " LVL 1", x + 52, y + 41);
		batch.end();
		batch.setShader(null);
		batch.begin();
	}

	@Override
	public Vector2 getCenter() {
		return null;
	}

	public Vector2 getCoinCenter() {
		return new Vector2(x + 50 + coins.getKeyFrame(0).getRegionWidth() / 2, y + coins.getKeyFrame(0).getRegionHeight() / 2);
	}
}

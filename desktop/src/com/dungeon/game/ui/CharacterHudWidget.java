package com.dungeon.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.Engine;
import com.dungeon.engine.ui.widget.AbstractWidget;
import com.dungeon.engine.ui.widget.Widget;
import com.dungeon.engine.viewport.ViewPort;
import com.dungeon.game.player.Player;
import com.dungeon.game.resource.Resources;

public class CharacterHudWidget extends AbstractWidget implements Widget {
	private final Animation<TextureRegion> coins;
	private final Animation<TextureRegion> face;
	private final Animation<TextureRegion> bars;
	private final Animation<TextureRegion> nameplate;
	private final Animation<TextureRegion> health;
	private final Animation<TextureRegion> energy;
	private final Animation<TextureRegion> experience;
	private final BitmapFont font;
	private final BitmapFont chubbyFont;
	private final Player player;
	private final ShaderProgram shaderOutline;
	private final Color outlineColor;
	private final ViewPort viewPort;

	private final int healthWidth;
	private final int energyWidth;
	private final int experienceWidth;

	public CharacterHudWidget(Player player, ViewPort viewPort) {
		face = getFace(player);
		coins = Resources.animations.get("coin");
 		bars = Resources.animations.get("hud_bars");
		nameplate = Resources.animations.get("hud_nameplate");
		health = Resources.animations.get("hud_health");
		energy = Resources.animations.get("hud_energy");
		experience = Resources.animations.get("hud_experience");
		font = Resources.fonts.get(Resources.DEFAULT_FONT);
		chubbyFont = Resources.fonts.get("chubby-9");
		this.player = player;
		shaderOutline = Resources.shaders.get("df_vertex.glsl|outline_border_fragment.glsl");
		height = 48;
		width = 144;
		this.viewPort = viewPort;
		outlineColor = new Color(0x504057ff);
		healthWidth = health.getKeyFrame(0).getRegionWidth();
		energyWidth = energy.getKeyFrame(0).getRegionWidth();
		experienceWidth = experience.getKeyFrame(0).getRegionWidth();
	}

	private Animation<TextureRegion> getFace(Player player) {
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
		batch.setColor(player.getColor());
		batch.draw(nameplate.getKeyFrame(Engine.time()), x + 23, y + 28);
		batch.setColor(Color.WHITE);
		batch.draw(bars.getKeyFrame(Engine.time()), x + 47, y + 11);

		// Display health
		TextureRegion keyFrame = health.getKeyFrame(Engine.time());
		float healthFill = player.getAvatar().getHealth() / player.getAvatar().getMaxHealth();
		keyFrame.setRegionX((int)((1f - healthFill) * healthWidth));
		keyFrame.setRegionWidth((int)(healthFill * healthWidth));
		batch.draw(keyFrame, x + 47, y + 22);

		// Display energy
		keyFrame = energy.getKeyFrame(Engine.time());
		float energyFill = player.getAvatar().getEnergy() / player.getAvatar().getMaxEnergy();
		keyFrame.setRegionX((int)((1f - energyFill) * energyWidth));
		keyFrame.setRegionWidth((int)(energyFill * energyWidth));
		batch.draw(keyFrame, x + 47, y + 16);

		// Display experience
		batch.draw(experience.getKeyFrame(Engine.time()), x + 47, y + 11);

		// Display mug
		batch.draw(face.getKeyFrame(Engine.time()), x, y);

		// Display coins
		batch.draw(coins.getKeyFrame(Engine.time()), x + 50, y);
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

package com.dungeon.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dungeon.engine.controller.player.PlayerControlBundle;
import com.dungeon.engine.render.effect.FadeEffect;
import com.dungeon.engine.resource.ResourceManager;
import com.dungeon.game.character.assassin.AssassinFactory;
import com.dungeon.game.character.thief.ThiefFactory;
import com.dungeon.game.character.witch.WitchFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharacterSelection {

	private static final int CHARACTER_COUNT = 3;
	private int currentPlayer = 0;
	private List<Slot> slots = new ArrayList<>(4);

	public static class Slot {
		public PlayerControlBundle control;
		public int playerId;
		public int characterId = 0;
		public Slot(PlayerControlBundle control, int playerId) {
			this.control = control;
			this.playerId = playerId;
		}
	}

	// Rendering stuff
	private SpriteBatch batch;
	private Texture playerCharacterScreen;

	public void initialize() {
		batch = new SpriteBatch();
		playerCharacterScreen = ResourceManager.getTexture("character_selection.png");
	}

	public void dispose() {
		playerCharacterScreen.dispose();
	}

	public boolean addControl(PlayerControlBundle control) {
		if (slots.size() < 4) {
			slots.add(new Slot(control, ++currentPlayer));
			return true;
		} else {
			return false;
		}
	}

	public void selectNextCharacter(PlayerControlBundle control) {
		getSlot(control).ifPresent(s -> {
			s.characterId = (s.characterId + 1) % CHARACTER_COUNT;
		});
	}

	public void selectPrevCharacter(PlayerControlBundle control) {
		getSlot(control).ifPresent(s -> {
			s.characterId = (s.characterId - 1 + CHARACTER_COUNT) % CHARACTER_COUNT;
		});
	}

	public void confirmSelection(PlayerControlBundle control) {
		// TODO Only confirm when all active slots have confirmed
		GameState.addRenderEffect(FadeEffect.fadeOut(GameState.time(), () -> GameState.startNewGame(slots)));
	}

	private Optional<Slot> getSlot(PlayerControlBundle control) {
		for (Slot s : slots) {
			if (s.control == control) {
				return Optional.of(s);
			}
		}
		return Optional.empty();
	}

	private Animation<TextureRegion> getAnimation(int characterId) {
		if (characterId == 0) {
			return ResourceManager.getAnimation(WitchFactory.WITCH_WALK);
		} else if (characterId == 1) {
			return ResourceManager.getAnimation(ThiefFactory.THIEF_WALK);
		} else {
			return ResourceManager.getAnimation(AssassinFactory.ASSASSIN_WALK);
		}
	}

	public void render() {
		batch.begin();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.draw(playerCharacterScreen, 0, 0, playerCharacterScreen.getWidth() * 4, playerCharacterScreen.getHeight() * 4);
		int x = 50, y = 80;
		for (Slot s : slots) {
			TextureRegion keyFrame = getAnimation(s.characterId).getKeyFrame(GameState.time());
			batch.draw(keyFrame, (x - keyFrame.getRegionWidth() / 2) * 4, (y - keyFrame.getRegionHeight() / 2) * 4, keyFrame.getRegionWidth() * 4, keyFrame.getRegionHeight() * 4);
			x += 100;
		}
		batch.end();
	}

}

package com.dungeon.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dungeon.engine.controller.player.PlayerControl;
import com.dungeon.engine.entity.PlayerCharacter;
import com.dungeon.game.character.Assasin;
import com.dungeon.game.character.Thief;
import com.dungeon.game.character.Witch;
import com.dungeon.game.level.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CharacterSelection {

	private static final int CHARACTER_COUNT = 3;
	private int currentPlayer = 0;
	private final GameState state;
	private List<Slot> slots = new ArrayList<>(4);

	public static class Slot {
		public PlayerControl control;
		public int playerId;
		public int characterId = 0;
		public Slot(PlayerControl control, int playerId) {
			this.control = control;
			this.playerId = playerId;
		}
	}

	// Rendering stuff
	private SpriteBatch batch;
	private Texture playerCharacterScreen;

	public CharacterSelection(GameState state) {
		this.state = state;
	}

	public void initialize() {
		batch = new SpriteBatch();
		playerCharacterScreen = new Texture("character_selection.png");
	}

	public void dispose() {
		playerCharacterScreen.dispose();
	}

	public boolean addControl(PlayerControl control) {
		if (slots.size() < 4) {
			slots.add(new Slot(control, ++currentPlayer));
			System.out.println("Added player!");
			return true;
		} else {
			return false;
		}
	}

	public void selectNextCharacter(PlayerControl control) {
		getSlot(control).ifPresent(s -> {
			s.characterId = (s.characterId + 1) % CHARACTER_COUNT;
			System.out.println("Player " + s.playerId + " switched to character " + s.characterId);
		});
	}

	public void selectPrevCharacter(PlayerControl control) {
		getSlot(control).ifPresent(s -> {
			s.characterId = (s.characterId - 1 + CHARACTER_COUNT) % CHARACTER_COUNT;
			System.out.println("Player " + s.playerId + " switched to character " + s.characterId);
		});
	}

	public void confirmSelection(PlayerControl control) {
		// TODO Only confirm when all active slots have confirmed
		state.startNewLevel(slots);
	}

	private Optional<Slot> getSlot(PlayerControl control) {
		for (Slot s : slots) {
			if (s.control == control) {
				return Optional.of(s);
			}
		}
		return Optional.empty();
	}

	private Animation<TextureRegion> getAnimation(int characterId) {
		if (characterId == 0) {
			return state.getTilesetManager().getCharactersTileset().WITCH_WALK_ANIMATION;
		} else if (characterId == 1) {
			return state.getTilesetManager().getCharactersTileset().THIEF_WALK_ANIMATION;
		} else {
			return state.getTilesetManager().getCharactersTileset().ASSASIN_WALK_ANIMATION;
		}
	}

	public void render() {
		batch.begin();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.draw(playerCharacterScreen, 0, 0, playerCharacterScreen.getWidth() * 4, playerCharacterScreen.getHeight() * 4);
		int x = 50, y = 80;
		for (Slot s : slots) {
			TextureRegion keyFrame = getAnimation(s.characterId).getKeyFrame(state.getStateTime());
			batch.draw(keyFrame, (x - keyFrame.getRegionWidth() / 2) * 4, (y - keyFrame.getRegionHeight() / 2) * 4, keyFrame.getRegionWidth() * 4, keyFrame.getRegionHeight() * 4);
			x += 100;
		}
		batch.end();
	}

}

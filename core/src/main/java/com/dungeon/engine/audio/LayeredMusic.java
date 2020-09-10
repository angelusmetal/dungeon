package com.dungeon.engine.audio;

public class LayeredMusic {
	private final String intro;
	private final String loop;
	private final int bpm;

	public LayeredMusic(String intro, String loop, int bpm) {
		this.intro = intro;
		this.loop = loop;
		this.bpm = bpm;
	}

	public String getIntro() {
		return intro;
	}

	public String getLoop() {
		return loop;
	}

	public int getBpm() {
		return bpm;
	}
}

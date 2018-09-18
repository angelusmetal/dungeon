package com.dungeon.engine.entity;

import com.dungeon.engine.Engine;

public interface TimeGradient {
	float get();

	static TimeGradient fadeIn(float startTime, float duration) {
		return () -> (Engine.time() - startTime) / duration;
	}

	static TimeGradient fadeOut(float startTime, float duration) {
		return () -> 1 - (Engine.time() - startTime) / duration;
	}

	static TimeGradient crossFade(float start, float end, float startTime, float duration) {
		return () -> {
			float fade = (Engine.time() - startTime) / duration;
			return start * fade + end * (1 - fade);
		};
	}
}

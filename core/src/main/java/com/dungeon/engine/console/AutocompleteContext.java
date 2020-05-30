package com.dungeon.engine.console;

import java.util.List;

/**
 * Result of an autocomplete search. Will contain a list of possible matches and the largest common
 * prefix.
 */
public class AutocompleteContext {
	private final List<String> matches;
	private final String commonPrefix;

	AutocompleteContext(List<String> matches, String commonPrefix) {
		this.matches = matches;
		this.commonPrefix = commonPrefix;
	}

	public List<String> getMatches() {
		return matches;
	}

	public String getCommonPrefix() {
		return commonPrefix;
	}
}

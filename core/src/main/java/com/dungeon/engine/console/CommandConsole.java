package com.dungeon.engine.console;

import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandConsole {

	private final StringBuilder currentCommand = new StringBuilder();
	private final List<String> commandHistory = new ArrayList<>();
	private final Map<String, Consumer<List<String>>> commands = new HashMap<>();
	private final Map<Integer, Runnable> keyBindings = new HashMap<>();
	private final InputProcessor inputProcessor = new AbstractInputProcessor() {
		@Override public boolean keyDown(int keycode) {
			Runnable action = keyBindings.get(keycode);
			if (action != null) {
				action.run();
				return true;
			} else {
				return false;
			}
		}
		@Override public boolean keyTyped(char character) {
			if (character == 0) {
				return false;
			} else if (character == 8) {
				commandBackspace();
			} else if (character == 9) {
				List<String> options = commandAutocomplete();
				if (options.size() > 1) {
					options.forEach(opt -> System.out.println("  " + opt));
				}
			} else if (character == 13) {
//				commandExecute();
			} else {
				commandAppend(character);
			}
			return true;
		}
	};

	public void bindKey(int keycode, Runnable runnable) {
		keyBindings.put(keycode, runnable);
	}

	public void bindCommand(String command, Consumer<List<String>> consumer) {
		commands.put(command, consumer);
	}

	public String getCurrentCommand() {
		return currentCommand.toString();
	}

	public void commandAppend(char character) {
		currentCommand.append(character);
	}

	public void commandSet(String command) {
		currentCommand.setLength(0);
		currentCommand.append(command);
	}

	public void commandBackspace() {
		if (currentCommand.length() > 0) {
			currentCommand.setLength(currentCommand.length() - 1);
		}
	}

	public List<String> commandAutocomplete() {
		final String command = currentCommand.toString();
		// Find all commands that start with the current command
		List<String> matches = commands.keySet().stream().filter(c -> c.startsWith(command)).collect(Collectors.toList());
		if (matches.isEmpty()) {
			// No match
			return Collections.emptyList();
		}
		String firstMatch = matches.get(0);
		String prefix = command;
		// If there are multiple matches, find the largest common prefix
		for (int i = command.length(); i <= firstMatch.length(); ++i) {
			final int index = i;
			if (!matches.stream().allMatch(c -> c.startsWith(firstMatch.substring(0, index)))) {
				break;
			}
			prefix = firstMatch.substring(0, index);
		}
		commandSet(prefix + " ");
		final String commonPrefix = prefix;
		// Return all the commands starting with the common prefix
		return commands.keySet().stream().filter(c -> c.startsWith(commonPrefix)).collect(Collectors.toList());
	}

	public void commandExecute() {
		String command = currentCommand.toString().trim();
		commandHistory.add(command);
		currentCommand.setLength(0);
		List<String> tokens = Arrays.stream(command.split(" ")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
		if (tokens.size() > 0) {
			commands.getOrDefault(tokens.get(0), s -> commandUnknown(tokens.get(0))).accept(tokens);
		}
	}

	private void commandUnknown(String command) {
		System.err.println("Unknown command: " + command);
	}

	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}
}

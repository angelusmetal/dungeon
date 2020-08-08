package com.dungeon.engine.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.dungeon.engine.controller.AbstractInputProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Console {

	private final StringBuilder currentCommand = new StringBuilder();
	private final List<String> commandHistory = new ArrayList<>();
	private int commandHistoryLevel = 0;
	private final TokenContext rootContext = new TokenContext();
	private final Map<String, ConsoleVar> vars = new HashMap<>();
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
					options.forEach(opt -> output.print("  " + opt));
				}
			} else if (character == 13 || (character >= '\uF700' && character <= '\uF703')) {
//				commandExecute();
			} else if (character >= 32) {
				commandAppend(character);
			}
			return true;
		}
	};

	private ConsoleOutput output = (text, color) -> Gdx.app.log("Console", text);

	public Console() {
		// Add get and set command for accessing variables
		bindExpression("get", tokens -> {
			if (tokens.isEmpty()) {
				output.print("Known variables:");
				vars.keySet().stream().sorted().forEach(var -> print ("  - " + var));
			} else {
				int maxNameLength = tokens.stream().map(String::length).max(Integer::compareTo).orElse(6);
				tokens.forEach(var -> {
					ConsoleVar value = vars.get(var);
					if (value != null) {
						output.print("  " + String.format("%1$" + maxNameLength + "s", var) + ": " + value.getter().get());
					} else {
						output.print("  " + String.format("%1$" + maxNameLength + "s", var) + ": <no such var>");
					}
				});
			}
			return true;
		}, () -> vars.keySet().stream().sorted());
		bindExpression("set", tokens -> {
			if (tokens.size() != 2) {
				output.print("Usage: set <var> <value>");
			} else {
				ConsoleVar var = vars.get(tokens.get(0));
				if (var != null) {
					try {
						var.setter().accept(tokens.get(1));
					} catch (RuntimeException e) {
						output.print(e.getMessage());
					}
				} else {
					output.print("No such var '" + tokens.get(0) + "'");
				}
			}
			return true;
		}, () -> vars.keySet().stream().sorted());

		// Add bindings for up and down for cycling through command history
		bindKey(Input.Keys.UP, this::historyUp);
		bindKey(Input.Keys.DOWN, this::historyDown);

		// Initialize command history
		commandHistory.add("");
	}

	public void bindKey(int keycode, Runnable runnable) {
		keyBindings.put(keycode, runnable);
	}

	public void bindExpression(String command, ConsoleExpression expression) {
		rootContext.findOrCreateDescendent(tokenize(command))
				.setExpression(expression);
	}

	public void bindExpression(String command, ConsoleExpression expression, Supplier<Stream<String>> childrenResolver) {
		TokenContext descendent = rootContext.findOrCreateDescendent(tokenize(command));
		descendent.setExpression(expression);
		descendent.setChildrenResolver(childrenResolver);
	}

	public void bindVar(ConsoleVar var) {
		vars.put(var.getName(), var);
	}

	public void setCurrentCommand(String command) {
		currentCommand.setLength(0);
		currentCommand.append(command);
	}

	public String getCurrentCommand() {
		return currentCommand.toString();
	}

	public void commandAppend(char character) {
		currentCommand.append(character);
	}

	public void commandBackspace() {
		if (currentCommand.length() > 0) {
			currentCommand.setLength(currentCommand.length() - 1);
		}
	}

	public List<String> commandAutocomplete() {
		String command = currentCommand.toString();
		List<String> tokens = tokenize(command);
		// If there's whitespace at the end, add an extra token to indicate an attempt to find another token
		if (command.endsWith(" ")) {
			tokens.add("");
		}
		AutocompleteContext autocomplete = rootContext.autocomplete(tokens);
		if (!tokens.isEmpty() && !autocomplete.getMatches().isEmpty()) {
			// If there's exactly one match, add a space at the end
			String suffix = autocomplete.getMatches().size() == 1 ? " " : "";
			// Replace the last token with an expanded version
			tokens.set(tokens.size() - 1, autocomplete.getCommonPrefix());
			// Update the current command
			setCurrentCommand(String.join(" ", tokens) + suffix);
		}
		return autocomplete.getMatches();
	}

	public void commandExecute() {
		String command = currentCommand.toString().trim();
		if (!command.isEmpty()) {
			output.print("> " + command, Color.RED);
			commandHistory.remove(commandHistory.size() - 1);
			commandHistory.add(command);
			commandHistory.add("");
			commandHistoryLevel = commandHistory.size() - 1;
		}
		currentCommand.setLength(0);
		List<String> tokens = tokenize(command);
		if (!rootContext.evaluate(tokens) && !tokens.isEmpty()) {
			commandUnknown(tokens.get(0));
		}
	}

	private void commandUnknown(String command) {
		output.print("Unknown command: " + command);
	}

	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}

	public void setOutput(ConsoleOutput output) {
		this.output = output;
	}

	public ConsoleOutput getOutput() {
		return output;
	}

	public void print(String message) {
		output.print(message);
	}

	private void historyUp() {
		if (commandHistoryLevel > 0) {
			commandHistory.set(commandHistoryLevel, currentCommand.toString());
			commandHistoryLevel--;
			setCurrentCommand(commandHistory.get(commandHistoryLevel));
		}
	}

	private void historyDown() {
		if (commandHistoryLevel < commandHistory.size() - 1) {
			commandHistory.set(commandHistoryLevel, currentCommand.toString());
			commandHistoryLevel++;
			setCurrentCommand(commandHistory.get(commandHistoryLevel));
		}
	}

	private List<String> tokenize(String sequence) {
		return Stream.of(sequence.split(" ")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}
}

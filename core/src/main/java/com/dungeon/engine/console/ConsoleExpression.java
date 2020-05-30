package com.dungeon.engine.console;

import java.util.function.Consumer;
import java.util.List;

@FunctionalInterface
public interface ConsoleExpression {
	/**
	 * Evaluate an expression
	 * @param arguments Input arguments
	 * @param output Output
	 * @return Whether this expression could be successfully evaluated
	 */
	boolean evaluate(List<String> arguments, Consumer<String> output);
}

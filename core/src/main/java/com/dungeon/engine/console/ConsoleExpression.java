package com.dungeon.engine.console;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ConsoleExpression {
	/**
	 * Evaluate an expression
	 * @param arguments Input arguments
	 * @return Whether this expression could be successfully evaluated
	 */
	boolean evaluate(List<String> arguments);

	interface FloatConsumer { boolean accept(float argument); }

	static ConsoleExpression of(Runnable method) {
		return arguments -> {
			method.run();
			return true;
		};
	}

	static ConsoleExpression of(Consumer<String> method) {
		return arguments -> {
			if (!arguments.isEmpty()) {
				method.accept(arguments.get(0));
				return true;
			} else {
				return false;
			}
		};
	}

	static ConsoleExpression of(IntConsumer method) {
		return arguments -> {
			if (!arguments.isEmpty()) {
				method.accept(Integer.parseInt(arguments.get(0)));
				return true;
			} else {
				return false;
			}
		};
	}

	static ConsoleExpression of(FloatConsumer method) {
		return arguments -> {
			if (!arguments.isEmpty()) {
				method.accept(Float.parseFloat(arguments.get(0)));
				return true;
			} else {
				return false;
			}
		};
	}
}

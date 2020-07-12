package com.dungeon.engine.console;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TokenContext {

	private Map<String, TokenContext> children = new HashMap<>();
	private Supplier<Stream<String>> childrenResolver = () -> children.keySet().stream();
	private ConsoleExpression expression;

//	/**
//	 * Context with no associated expression
//	 */
//	public TokenContext() {
//		this(null);
//	}
//
//	/**
//	 * Token with an associated expression
//	 * @param expression
//	 */
//	public TokenContext(ConsoleExpression expression) {
//		this.expression = expression;
//		this.childrenResolver = () -> children.keySet().stream();
//	}
//
//	/**
//	 * Token with an associated expression
//	 * @param expression
//	 */
//	public TokenContext(ConsoleExpression expression, Supplier<Stream<String>> childrenResolver) {
//		this.expression = expression;
//		this.childrenResolver = childrenResolver;
//	}

	/**
	 * Find child associated with provided token, or Optional.empty()
	 */
	Optional<TokenContext> findChild(String token) {
		return Optional.ofNullable(children.get(token));
	}

	public AutocompleteContext autocomplete(List<String> tokens) {
		if (tokens.isEmpty()) {
			return autocomplete("");
		} else if (tokens.size() == 1) {
			return autocomplete(tokens.get(0));
		} else {
			// Delegate autocomplete to children
			return findChild(tokens.get(0))
					.map(child -> child.autocomplete(tokens.subList(1, tokens.size())))
					.orElse(new AutocompleteContext(Collections.emptyList(), tokens.get(tokens.size() - 1)));
		}
	}

	/**
	 * Do an autocomplete search based on the provided token
	 * @param token Probably incomplete token
	 * @return an {@link AutocompleteContext} describing the possible resolutions
	 */
	private AutocompleteContext autocomplete(String token) {
		// Find all children that start with the current token
		List<String> matches = childrenResolver.get().filter(c -> c.startsWith(token)).collect(Collectors.toList());
		if (matches.isEmpty()) {
			// No match
			return new AutocompleteContext(Collections.emptyList(), "");
		} else if (matches.size() == 1) {
			// Single match
			return new AutocompleteContext(Collections.singletonList(matches.get(0)), matches.get(0));
		} else {
			String firstMatch = matches.get(0);
			String prefix = token;
			// If there are multiple matches, find the largest common prefix
			for (int i = token.length(); i <= firstMatch.length(); ++i) {
				final int index = i;
				if (!matches.stream().allMatch(c -> c.startsWith(firstMatch.substring(0, index)))) {
					break;
				}
				prefix = firstMatch.substring(0, index);
			}
			final String commonPrefix = prefix;
			// Return all the commands starting with the common prefix
			return new AutocompleteContext(childrenResolver.get().filter(c -> c.startsWith(commonPrefix)).sorted().collect(Collectors.toList()), commonPrefix);
		}
	}

	public void setExpression(ConsoleExpression expression) {
		this.expression = expression;
	}

	public void setChildrenResolver(Supplier<Stream<String>> childrenResolver) {
		this.childrenResolver = childrenResolver;
	}

	public boolean evaluate(List<String> tokens) {
		// An expression is bound to this context, so all tokens are passed to it for parsing
		if (expression != null) {
			return expression.evaluate(tokens);
		}
		// No expression is bound to this context, so continue walking children to find one
		else {
			// No more tokens; this cannot be evaluated
			if (tokens.isEmpty()) {
				return false;
			}
			Optional<TokenContext> child = findChild(tokens.get(0));
			// Continue traversing children, with the remaining tokens
			return child.map(tokenContext -> tokenContext.evaluate(tokens.stream().skip(1).collect(Collectors.toList())))
					// No child matching next token; this cannot be evaluated
					.orElse(false);
		}
	}

	public TokenContext findOrCreateDescendent(List<String> tokens) {
		TokenContext currentContext = this;
		for (String token : tokens) {
			currentContext = currentContext.addOrGetChild(token);
		}
		return currentContext;
	}

	public TokenContext addOrGetChild(String token) {
		return children.computeIfAbsent(token, k -> new TokenContext());
	}

}

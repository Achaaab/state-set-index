package com.github.achaaab.ssi;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.min;
import static java.util.Optional.ofNullable;

/**
 * string index allowing fuzzing match using State Set Index algorithm
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.0
 */
public class StateSetIndex {

	private final Set<String> strings;
	private final Function<Character, Character> alphabetMappingFunction;
	private final int size;

	private State root;

	/**
	 * Builds a new index using given data.
	 *
	 * @param strings strings to index
	 * @param alphabetMappingFunction alphabet mapping function
	 * @param size maximum index length
	 */
	public StateSetIndex(
			Set<String> strings,
			Function<Character, Character> alphabetMappingFunction,
			int size) {

		this.strings = strings;
		this.alphabetMappingFunction = alphabetMappingFunction;
		this.size = size;

		build();
	}

	/**
	 * Build this index using the given strings.
	 *
	 * @since 0.0.0
	 */
	private void build() {

		root = new State();
		strings.forEach(this::addString);
	}

	/**
	 * Adds a string to this index.
	 *
	 * @param string string to add
	 * @since 0.0.0
	 */
	private void addString(String string) {

		var indexedSubstringLength = min(size, string.length());
		var indexedSubstring = string.substring(0, indexedSubstringLength);

		var currentState = root;

		for (var character : indexedSubstring.toCharArray()) {

			var mappedCharacter = alphabetMappingFunction.apply(character);
			currentState = currentState.getOrCreateChild(mappedCharacter);
		}

		currentState.addString(string);
	}

	/**
	 * Find the nearest string in this index.
	 *
	 * @param string string to find
	 * @param threshold maximum acceptable edit distance
	 * @return nearest string in this index, if the edit distance does not exceed the given threshold
	 * @since 0.0.0
	 */
	public Optional<String> find(String string, int threshold) {

		var states = root.getReachableStates(0, threshold);

		var indexedSubstringLength = min(size, string.length());
		var indexedSubstring = string.substring(0, indexedSubstringLength);

		for (var character : indexedSubstring.toCharArray()) {

			var mappedCharacter = alphabetMappingFunction.apply(character);

			var nextStates = new StateSet();

			states.forEach((state, cost) -> {

				var newStates = new StateSet();

				// deletion
				if (cost + 1 <= threshold) {
					newStates.add(state, cost + 1);
				}

				state.getTransitions().forEach((transitionCharacter, child) -> {

					if (transitionCharacter == mappedCharacter) {

						// match
						newStates.add(child, cost);

					} else if (cost + 1 <= threshold) {

						// substitution
						newStates.add(child, cost + 1);
					}
				});

				// insertion
				newStates.forEach((newState, newScore) ->
						nextStates.addAll(newState.getReachableStates(newScore, threshold)));
			});

			states = nextStates;
		}

		var candidates = states.strings();

		return getBestCandidate(string, candidates, threshold);
	}

	/**
	 * @param string string to match
	 * @param candidates candidates (including false positives)
	 * @param threshold maximum acceptable edit distance
	 * @return candidate with the lowest edit distance, if it does not exceed the given threshold
	 * @since 0.0.0
	 */
	private Optional<String> getBestCandidate(String string, Set<String> candidates, int threshold) {

		String bestCandidate = null;
		int bestDistance = -1;

		var levenshtein = new LevenshteinDistance(threshold);

		for (var candidate : candidates) {

			var distance = levenshtein.apply(candidate, string);

			if (distance != -1 && (bestCandidate == null || distance < bestDistance)) {

				bestCandidate = candidate;
				bestDistance = distance;

				if (bestDistance < threshold) {
					levenshtein = new LevenshteinDistance(threshold = bestDistance);
				}
			}
		}

		return ofNullable(bestCandidate);
	}
}
package com.github.achaaab.ssi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * state of an NFA (Nondeterministic Finite Automaton)
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.0
 */
public class State {

	public static final Map<Character, State> EMPTY_TRANSITIONS = Map.of();
	public static final Set<String> EMPTY_STRINGS = Set.of();

	private Map<Character, State> transitions;
	private Set<String> strings;

	/**
	 * Creates an empty state with no transitions and no strings.
	 *
	 * @since 0.0.0
	 */
	public State() {

		transitions = EMPTY_TRANSITIONS;
		strings = EMPTY_STRINGS;
	}

	/**
	 * Gets the child associated with the given character if such a transition exists.
	 * Otherwise, creates a child and a transition from this state to the created child.
	 *
	 * @param character transition character
	 * @return retrieved or created child
	 * @since 0.0.0
	 */
	public State getOrCreateChild(char character) {

		return getChild(character).
				orElseGet(() -> createChild(character));
	}

	/**
	 * Gets the child associated to this state with the given transition character.
	 *
	 * @param character transition character
	 * @return child associated to the given character
	 * @since 0.0.0
	 */
	public Optional<State> getChild(char character) {

		return transitions == null ?
				empty() :
				ofNullable(transitions.get(character));
	}

	/**
	 * Associates a child to this state with the given transition character.
	 *
	 * @param character transition character
	 * @return created child
	 * @since 0.0.0
	 */
	private State createChild(char character) {

		if (transitions == EMPTY_TRANSITIONS) {
			transitions = new HashMap<>();
		}

		var child = new State();
		transitions.put(character, child);
		return child;
	}

	/**
	 * Computes the set states reachable with at most {@code maximumDistance - currentDistance} transitions
	 * from this state.
	 *
	 * @param currentDistance current distance
	 * @param maximumDistance maximum distance
	 * @return set of reachable states
	 * @since 0.0.0
	 */
	public StateSet getReachableStates(int currentDistance, int maximumDistance) {

		var reachableStates = new StateSet();
		addReachableStates(reachableStates, currentDistance, maximumDistance);
		return reachableStates;
	}

	/**
	 * Adds states reachable with at most {@code maximumDistance - currentDistance} transitions from this state.
	 *
	 * @param reachableStates set in which to add reachable states
	 * @param currentDistance current distance
	 * @param maximumDistance maximum distance
	 * @since 0.0.0
	 */
	public void addReachableStates(StateSet reachableStates, int currentDistance, int maximumDistance) {

		if (currentDistance <= maximumDistance) {

			reachableStates.add(this, currentDistance);

			if (currentDistance < maximumDistance) {

				transitions.values().forEach(child ->
						child.addReachableStates(reachableStates, currentDistance + 1, maximumDistance));
			}
		}
	}

	/**
	 * Adds a candidate string to this state.
	 *
	 * @param string candidate string
	 * @since 0.0.0
	 */
	public void addString(String string) {

		if (strings == EMPTY_STRINGS) {
			strings = new HashSet<>();
		}

		strings.add(string);
	}

	/**
	 * @return transitions from this state
	 * @since 0.0.0
	 */
	public Map<Character, State> getTransitions() {
		return transitions;
	}

	/**
	 * @return candidate strings at this state
	 * @since 0.0.0
	 */
	public Set<String> strings() {
		return strings;
	}
}
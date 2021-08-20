package com.github.achaaab.ssi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Set of states with their associated cost.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.0
 */
public class StateSet extends HashMap<State, Integer> {

	/**
	 * Adds a state with a cost to this set.
	 * If this sets already contains the given state with a higher cost, replaces it.
	 *
	 * @param state state to add
	 * @param cost cost to associate to the given state
	 * @since 0.0.0
	 */
	public void add(State state, int cost) {

		var existingCost = get(state);

		if (existingCost == null || existingCost > cost) {
			put(state, cost);
		}
	}

	/**
	 * Adds each state of the given set to this set.
	 *
	 * @param stateSet set of states to add
	 * @since 0.0.0
	 */
	public void addAll(StateSet stateSet) {
		stateSet.forEach(this::add);
	}

	/**
	 * @return union of candidate strings from each state in this set
	 * @since 0.0.0
	 */
	public Set<String> strings() {

		var children = keySet().stream();

		return children.
				map(State::strings).
				flatMap(Collection::stream).
				collect(toSet());
	}
}
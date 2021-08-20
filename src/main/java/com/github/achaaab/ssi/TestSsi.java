package com.github.achaaab.ssi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * test class
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.0
 */
public class TestSsi {

	private static final int THRESHOLD = 2;
	private static final int INDEX_SIZE = 6;
	private static final Set<String> STRINGS = Set.of("Müller", "Mueller", "Muentner", "Muster", "Mustermann");
	private static final String STRING_TO_MATCH = "Mustre";

	private static final Map<Character, Character> ALPHABET_MAPPING;

	static {

		ALPHABET_MAPPING = new HashMap<>();

		ALPHABET_MAPPING.put('M', '1');
		ALPHABET_MAPPING.put('u', '2');
		ALPHABET_MAPPING.put('e', '3');
		ALPHABET_MAPPING.put('l', '4');
		ALPHABET_MAPPING.put('r', '1');
		ALPHABET_MAPPING.put('ü', '2');
		ALPHABET_MAPPING.put('n', '3');
		ALPHABET_MAPPING.put('t', '4');
		ALPHABET_MAPPING.put('s', '1');
		ALPHABET_MAPPING.put('m', '2');
		ALPHABET_MAPPING.put('a', '3');
	}

	/**
	 * Runs a simple test based on the research paper example.
	 * 
	 * @param arguments none
	 * @since 0.0.0
	 */
	public static void main(String... arguments) {

		var stateSetIndex = new StateSetIndex(
				STRINGS,
				ALPHABET_MAPPING::get,
				INDEX_SIZE
		);

		var match = stateSetIndex.find(STRING_TO_MATCH, THRESHOLD);
		System.out.println(match);
	}
}
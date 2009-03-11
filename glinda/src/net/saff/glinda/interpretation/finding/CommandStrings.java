/**
 * 
 */
package net.saff.glinda.interpretation.finding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandStrings {
	private List<String> strings;
	private CommandStrings cachedRest;

	public CommandStrings(List<String> strings) {
		this.strings = strings;
	}

	public CommandStrings(String... strings) {
		this(Arrays.asList(strings));
	}

	public CommandStrings compressBrackets() {
		ArrayList<String> output = new ArrayList<String>();
		String currentWord = "";
		for (String inputString : strings) {
			if (inputString != null) {
				if (startsWithBracket(currentWord)) {
					currentWord += " " + inputString;
					if (endsWithBracket(inputString)) {
						output.add(currentWord);
						currentWord = "";
					}
				} else if (startsWithBracket(inputString)) {
					if (endsWithBracket(inputString)
							&& !inputString.contains("] "))
						output.add(inputString);
					else if (inputString.contains("] ")) {
						int lastEndBracket = inputString.lastIndexOf("] ");
						output
								.add(inputString.substring(0,
										lastEndBracket + 1));
						output.add(inputString.substring(lastEndBracket + 2));
					} else {
						currentWord = inputString;
					}
				} else {
					output.add(inputString);
				}
			}
		}
		return new CommandStrings(output);
	}

	private boolean endsWithBracket(String inputString) {
		return inputString.endsWith("]");
	}

	private boolean startsWithBracket(String string) {
		return string.startsWith("[");
	}

	public int length() {
		return strings.size();
	}

	public String getFirst() {
		if (strings.size() == 0)
			return "noMethodName";
		return strings.get(0);
	}

	@Override
	public boolean equals(Object obj) {
		return strings.equals(((CommandStrings) obj).strings);
	}

	public CommandStrings getRest() {
		if (cachedRest == null)
			computeCachedRest();
		return cachedRest;
	}

	private void computeCachedRest() {
		if (strings.size() == 0)
			cachedRest = new CommandStrings(Collections
					.<String> emptyList());
		cachedRest = new CommandStrings(strings.subList(1, strings.size()));
	}

	public String join() {
		String joined = "";
		for (String each : strings)
			joined += " " + each;
		return joined.trim();
	}

	public List<String> toList() {
		return strings;
	}

	public int size() {
		return strings.size();
	}
}
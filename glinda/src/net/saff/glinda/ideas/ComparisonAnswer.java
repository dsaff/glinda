package net.saff.glinda.ideas;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComparisonAnswer {

	private boolean firstIsBest;
	private final String[] commands;
	private List<Idea> doneIdeas = new ArrayList<Idea>();

	public ComparisonAnswer(boolean firstIsBest, String... commands) {
		this.firstIsBest = firstIsBest;
		this.commands = commands;
	}

	private ComparisonAnswer(boolean firstIsBest, Idea doneIdea,
			String... commands) {
		this(firstIsBest, commands);
		this.doneIdeas.add(doneIdea);
	}

	public boolean firstIsBest() {
		return firstIsBest;
	}

	public Collection<? extends String> getCommands() {
		return asList(commands);
	}

	public static ComparisonAnswer done(boolean firstIsBest, Idea doneIdea) {
		return new ComparisonAnswer(firstIsBest, doneIdea, doneIdea
				.doneString());
	}

	public List<Idea> getDoneIdeas() {
		return doneIdeas;
	}
}

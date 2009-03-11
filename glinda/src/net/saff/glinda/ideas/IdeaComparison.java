/**
 * 
 */
package net.saff.glinda.ideas;

import net.saff.glinda.ideas.correspondent.Correspondent;

public class IdeaComparison {
	private final Idea first;
	private final Idea next;

	public IdeaComparison(Idea first, Idea next) {
		this.first = first;
		this.next = next;
	}

	public String command() {
		return String.format("#before %s %s", first, next);
	}

	public IdeaComparison reverse() {
		return new IdeaComparison(next, first);
	}

	public IdeaComparison reverseIf(boolean shouldReverse) {
		if (shouldReverse)
			return reverse();
		return this;
	}

	void addDominator() {
		next.addDominator(first);
	}

	boolean firstDominates() {
		return next == null || next.getDominators().contains(first) || next.isDone();
	}

	String askWhichIsMoreImportant(Correspondent correspondent) {
		return correspondent.getAnswer("Which is more important?", "1) "
				+ first, "2) " + next, "d) mark done");
	}

	Idea askWhichIsDone(Correspondent correspondent) {
		String whichIsDone = correspondent.getAnswer("Which is done?", "1) "
				+ first, "2) " + next);
		if (whichIsDone.startsWith("1"))
			return first;
		else
			return next;
	}

	ComparisonAnswer nextIsDone(Correspondent correspondent) {
		Idea doneIdea = askWhichIsDone(correspondent);
		doneIdea.done();
		return ComparisonAnswer.done(doneIdea.equals(next), doneIdea);
	}

	ComparisonAnswer askUserIfFirstIsBest(Correspondent correspondent) {
		String moreImportant = askWhichIsMoreImportant(correspondent);
		if (moreImportant.startsWith("d"))
			return nextIsDone(correspondent);
		
		boolean firstIsBest = moreImportant.startsWith("1");
		IdeaComparison correctComparison = reverseIf(!firstIsBest);
		correctComparison.addDominator();
		return new ComparisonAnswer(firstIsBest, correctComparison.command());
	}

	ComparisonAnswer compare(Correspondent correspondent) {
		if (firstDominates())
			return new ComparisonAnswer(true);
		if (reverse().firstDominates())
			return new ComparisonAnswer(false);
		return askUserIfFirstIsBest(correspondent);
	}
}
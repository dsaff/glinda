/**
 * 
 */
package net.saff.glinda.ideas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.search.KeyValueMatcher;
import net.saff.glinda.ideas.search.SearchKey;
import net.saff.glinda.names.BracketedString;

public class Idea implements KeyValueMatcher, Serializable {
	List<Idea> dominators = new ArrayList<Idea>();
	Map<String, Boolean> interestedParties = new HashMap<String, Boolean>();
	Double estimate = null;
	private final BracketedString name;
	private String bucket;
	private boolean done = false;
	public static final int MAX_ANSWERS_PER_ESTIMATION_QUESTION = 20;
	public static final String CORRECT_ESTIMATE = "c) correct";
	public static final String[] ESTIMATIZE_ANSWERS = new String[] { "+) more",
			"-) less", "d) task done", CORRECT_ESTIMATE };

	public Idea(String name) {
		this.name = new BracketedString(name);
	}

	public List<Idea> getDominators() {
		return dominators;
	}

	public void addDominator(Idea first) {
		dominators.add(first);
	}

	@Override public String toString() {
		return name.toString();
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	@Override public boolean equals(Object obj) {
		if (! (obj instanceof Idea))
			return false;
		return name.equals(((Idea) obj).name);
	}

	public void interest(String interestedParty, boolean interested) {
		interestedParties.put(interestedParty, interested);
	}

	public boolean interested(String interestedParty) {
		return interestKnown(interestedParty)
				&& interestedParties.get(interestedParty);
	}

	public boolean interestKnown(String interestedParty) {
		return interestedParties.containsKey(interestedParty);
	}

	public void estimate(double estimate) {
		this.estimate = estimate;
	}

	public Double getEstimate() {
		return estimate != null ? estimate : 0.5;
	}

	public void bucket(String bucket) {
		this.bucket = bucket;
	}

	public boolean inBucket(String searchValue) {
		return searchValue == null ? bucket == null : searchValue
				.equals(bucket);
	}

	/* (non-Javadoc)
	 * @see net.saff.glinda.ideas.KeyValueMatcher#matches(net.saff.glinda.search.SearchKey, java.lang.String)
	 */
	public boolean matches(SearchKey searchKey, String searchValue) {
		switch (searchKey) {
		case interest:
			return interested(searchValue);
		case bucket:
			return inBucket(searchValue);
		}
		return false;
	}

	public String askForEstimate(Correspondent correspondent) {
		double currentEstimate = 0.5;

		String answer;
		int answerCount = 0;
		while (true) {
			answer = correspondent.getAnswer(String.format(
					"%s hours to complete '%s'?", currentEstimate, this),
					Idea.ESTIMATIZE_ANSWERS);

			if (answer.startsWith("+"))
				currentEstimate += 0.5;
			if (answer.startsWith("-"))
				currentEstimate -= 0.5;
			if (answer.startsWith("d"))
				return doneString();
			if (answer.startsWith("c")
					|| answerCount++ > Idea.MAX_ANSWERS_PER_ESTIMATION_QUESTION)
				return String.format("#estimate %s %s", this, currentEstimate);
		}
	}

	public String doneString() {
		return String.format("#done %s", this);
	}

	public boolean hasName(BracketedString name) {
		return this.name.equals(name);
	}

	public boolean estimateKnown() {
		return estimate != null;
	}

	public void dominates(Idea second) {
		before(second);
	}

	public void before(Idea second) {
		second.addDominator(this);
	}

	public BracketedString getName() {
		return name;
	}

	public boolean isDone() {
		return done;
	}

	public void done() {
		done = true;
	}
}
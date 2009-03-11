/**
 * 
 */
package net.saff.glinda.ideas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.search.KeyValueMatcher;
import net.saff.glinda.ideas.search.KeyValuePair;
import net.saff.glinda.ideas.search.SearchCriteria;
import net.saff.glinda.ideas.search.SearchKey;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.names.DoneListener;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.time.GlindaTimePoint;

public class IdeaList implements Iterable<Idea>, DoneListener, Serializable {
	private final LinkedHashMap<BracketedString, Idea> ideas = new LinkedHashMap<BracketedString, Idea>();

	public static final String[] INTEREST_ANSWERS = new String[] { "true",
			"false", "done" };

	public KeyValueMatcher add(String idea) {
		return get(new BracketedString(idea));
	}

	public List<String> asStringList() {
		ArrayList<String> returnThis = new ArrayList<String>();
		for (KeyValueMatcher idea : ideas.values()) {
			returnThis.add(idea.toString());
		}
		return returnThis;
	}

	public List<String> interestize(String interestedParty,
			Correspondent correspondent) {
		ArrayList<String> list = new ArrayList<String>();
		for (Idea idea : getIdeas()) {
			if (!idea.interestKnown(interestedParty)) {
				String answer = correspondent.getAnswer(String.format(
						"Is %s interested in %s?", interestedParty, idea),
						IdeaList.INTEREST_ANSWERS);
				if ("done".equals(answer))
					list.add(String.format("#done %s", idea));
				else
					list.add(String.format("#interest %s %s %s", idea,
							interestedParty, answer));
			}
		}

		return list;
	}

	public IdeaList find(final SearchCriteria searchCriteria) {
		IdeaList returnThis = new IdeaList();

		for (Idea idea : getIdeas())
			if (searchCriteria.match(idea))
				returnThis.add(idea);
		return returnThis;
	}

	public KeyValueMatcher add(Idea idea) {
		ideas.put(idea.getName(), idea);
		return idea;
	}

	public List<String> bucketize(Correspondent correspondent) {
		Iterable<Idea> unbucketed = find(new KeyValuePair(SearchKey.bucket,
				null));
		return new BucketValueSet().categorize(unbucketed, correspondent);
	}

	public List<String> estimatize(Correspondent correspondent) {
		ArrayList<String> returnThis = new ArrayList<String>();

		for (Idea idea : this) {
			if (!idea.estimateKnown())
				returnThis.add(idea.askForEstimate(correspondent));
		}

		return returnThis;
	}

	public List<String> backlog(final Correspondent correspondent,
			double hoursToPlan) {
		if (ideas.size() == 0)
			return Collections.emptyList();
		final ArrayList<String> returnThis = new ArrayList<String>();
		ArrayList<String> plan = new ArrayList<String>();
		double hoursPlanned = 0;

		Heap<Idea> heap = new Heap<Idea>(ideas.size(), new Comparator<Idea>() {
			public int compare(Idea i1, Idea i2) {
				ComparisonAnswer compare = new IdeaComparison(i1, i2)
						.compare(correspondent);
				returnThis.addAll(compare.getCommands());
				if (compare.firstIsBest())
					return -1;
				return 1;
			}
		});
		
		for (Idea each : getIdeas())
			heap.insert(each);

		while (hoursPlanned < hoursToPlan) {
			Idea first = heap.extract();
			double estimate = first.getEstimate();
			plan.add(String.format("%s %s", estimate, first));
			hoursPlanned += estimate;
		}

		returnThis.addAll(plan);
		return returnThis;
	}

	public Iterator<Idea> iterator() {
		return getIdeas().iterator();
	}

	@Override public String toString() {
		return getIdeas().toString();
	}

	@Override public boolean equals(Object obj) {
		IdeaList list = (IdeaList) obj;
		return new ArrayList<Idea>(list.getIdeas()).equals(new ArrayList<Idea>(
				getIdeas()));
	}

	public Collection<Idea> getIdeas() {
		return ideas.values();
	}

	public Idea get(BracketedString name) {
		Idea existingIdea = getExistingIdea(name);
		if (existingIdea != null)
			return existingIdea;

		return addNewIdea(name);
	}

	private Idea getExistingIdea(BracketedString name) {
		return ideas.get(name);
	}

	// TODO: cycle?
	public void done(BracketedString name, GlindaTimePoint now, RequirementGraph<Project> graph) {
		ideas.remove(name);
	}

	private Idea addNewIdea(BracketedString name) {
		Idea idea = new Idea(name.toString());
		add(idea);
		return idea;
	}

	public void addWithEstimate(String ideaName, double estimate) {
		Idea idea = new Idea(ideaName);
		idea.estimate(estimate);
		add(idea);
	}
}
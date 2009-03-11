/**
 * 
 */
package test.net.saff.glinda.units.ideas;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import net.saff.glinda.ideas.Idea;
import net.saff.glinda.ideas.IdeaList;
import net.saff.glinda.ideas.search.Everything;
import net.saff.glinda.ideas.search.KeyValuePair;
import net.saff.glinda.ideas.search.SearchCriteria;
import net.saff.glinda.ideas.search.SearchKey;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class HowDoesIdeaListAtAnyTime extends
		LoqBookDataPoints {
	@DataPoint public static IdeaList withInterestingIdea() {
		IdeaList list = new IdeaList();
		list.add(excellent);
		excellent.interest("the boss", true);
		return list;
	}

	@DataPoint public static IdeaList withBucket() {
		IdeaList list = new IdeaList();
		list.add(excellent);
		excellent.bucket("day");
		return list;
	}

	@DataPoint public static Idea excellent = new Idea("excellent");

	@DataPoint public static SearchCriteria bossInterested = new KeyValuePair(
			SearchKey.interest, "the boss");

	@DataPoint public static SearchCriteria dayBucket = new KeyValuePair(
			SearchKey.bucket, "day");

	@DataPoint public static IdeaList empty = new IdeaList();
	@DataPoint public static SearchCriteria everything = new Everything();

	private IdeaList list;

	public HowDoesIdeaListAtAnyTime(IdeaList list) {
		assumeNotNull(list);
		this.list = list;
	}

	@Theory public void maintainDataWhenFinding(SearchCriteria criteria) {
		assertThat(list.find(criteria), is(list.find(new Everything())
				.find(criteria)));
	}

	@Theory public void determineEquality(IdeaList list2, Idea idea) {
		assumeNotNull(list2);
		assumeThat(list.getIdeas(), not(hasItem(idea)));
		assumeThat(list2.getIdeas(), hasItem(idea));
		assertThat(list, not(list2));
	}

	@Theory public void displaySelf(String idea) {
		assumeNotNull(idea);
		list.add(idea);
		assertThat(list.toString(), containsString(idea));
	}

	@Theory public void rejectNullCorrespondentsInBacklog(double hours) {
		try {
			list.backlog(null, hours);
			fail("should have thrown exception");
		} catch (NullPointerException e) {
			// success!
		}
	}
}
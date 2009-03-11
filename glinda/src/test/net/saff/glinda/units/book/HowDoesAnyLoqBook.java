/**
 * 
 */
package test.net.saff.glinda.units.book;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.stubbedtheories.StubbedTheories;

import org.hamcrest.Matcher;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(StubbedTheories.class) public class HowDoesAnyLoqBook extends
		LoqBookDataPoints {
	private LoqBook book;

	public HowDoesAnyLoqBook(LoqBook book) {
		this.book = book;
	}
	
	@Theory public void displayAllGoalStatus(GlindaTimePoint now,
			BracketedString goalName) {
		book.now(now);
		book.getConcerns().startGoal(goalName);
		book.setNow(now);
		assertThat(book.status(null), hasLineContaining(goalName));
	}

	private Matcher<Iterable<String>> hasLineContaining(BracketedString bstring) {
		return hasLineContaining(bstring.toString());
	}

	private Matcher<Iterable<String>> hasLineContaining(String string) {
		return hasItem(containsString(string));
	}
}
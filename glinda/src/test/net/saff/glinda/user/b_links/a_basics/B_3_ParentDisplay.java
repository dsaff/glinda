/**
 * 
 */
package test.net.saff.glinda.user.b_links.a_basics;

import static com.domainlanguage.time.Duration.days;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.EnumSet;

import net.saff.glinda.book.DurationWaitTime;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class B_3_ParentDisplay extends CondCorrespondentOnDiskTest {
	@Test
	public void onlyAddParentOnce() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo A B");
		nowIs("2007-01-01 12:10:00");

		runStatusWithParents();
		w("! p:B <- A");
		w("> p:A (first: p:B until 2007-01-02 12:00:00)");
	}

	@Test
	public void breakOutTripParents() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#priorTo B A");
		r("#priorTo C A");
		r("#priorTo D B");
		r("#priorTo E B");
		r("#trip B");
		r("#trip A");
		nowIs("2007-01-01 12:10:00");

		runStatusWithParents();
		w("! p:A");
		w("  <- B");
		w("    <- D");
		w("    <- E");
		w("  <- C");
	}

	@Test
	public void breakOutTripParentsEvenWhenSingle() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#priorTo B A");
		r("#trip A");
		nowIs("2007-01-01 12:10:00");

		runStatusWithParents();
		w("! p:A");
		w("  <- B");
	}

	@Test
	public void tripsWithOnlyBlockedParentsAreNotActionable() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep C A");
		r("#nextStep C D");
		r("#wait D 7d");
		r("#trip A");
		nowIs("2007-01-01 12:10:00");

		runStatusWithParents();
		w("> p:C (next step: p:A)");
		w("> p:A (waiting on parent: C)");
		w("> p:D (waiting until 2007-01-08 12:00:00)");
	}

	@RunWith(Theories.class)
	public static class BlockedParentsUnits extends LoqBookDataPoints {
		@Theory
		public void tripsWithOnlyBlockedParentsAreNotActionableUnits(
				GlindaTimePoint now, BracketedString blockedParent) {
			LoqBook book = LoqBook.withDefaults();
			book.now(now);
			book.nextStep(blockedParent, new BracketedString("A"));
			book.nextStep(blockedParent, new BracketedString("D"));
			book.wait(book.getConcerns().findConcern(new BracketedString("D")),
					new DurationWaitTime(days(7)));
			book.getConcerns().findConcern(new BracketedString("A")).trip();
			book.setNow(now);

			assertThat(book.status(EnumSet.of(Flag.actionItemsFirst,
					Flag.withParents, Flag.showFullTimes)),
					hasItem(containsString("waiting on parent: "
							+ blockedParent)));
		}
	}
}

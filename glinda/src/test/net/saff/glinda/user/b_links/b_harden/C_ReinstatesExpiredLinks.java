/**
 * 
 */
package test.net.saff.glinda.user.b_links.b_harden;

import static org.junit.Assert.assertFalse;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.core.GlindaInvocation;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.JournalParser;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.OutgoingLinkFinder;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.tasks.IgnoredLinks;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class C_ReinstatesExpiredLinks extends CondCorrespondentOnDiskTest {
	@Test
	public void hardenReinstatesBrokenSoftLink() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#now 2007-01-03 12:00:00");
		r("#harden A");

		nowIs("2007-01-03 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:B <- A");
		w("> p:A (first: p:B until 01-04 12:00)");
	}

	@Test
	public void hardenReinstatesBrokenSoftLinkOneAmongMany() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo A C");
		r("#priorTo A D");
		r("#priorTo A E");
		r("#now 2007-01-03 12:00:00");
		r("#harden A");

		nowIs("2007-01-03 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:B <- A");
		w("! p:C");
		w("! p:D");
		w("! p:E");
		w("> p:A (first: p:B until 01-04 12:00)");
	}

	@Test
	public void hardenSkipsDoneChildren() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo A C");
		r("#done B");
		r("#now 2007-01-03 12:00:00");
		r("#harden A");

		nowIs("2007-01-03 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:C <- A");
		w("> p:A (first: p:C until 01-04 12:00)");
	}

	@Test
	public void hardenWorksOnBigJournal() throws LoqCommandExecutionException {
		GlindaInvocation invocation = GlindaInvocation.fromLocation(
				"src/test/resources/108kjournal.txt", "status", "-withTimes",
				"-actionItemsFirst", "-withParents");
		JournalParser<?> parser = invocation.readParser();
		LoqBook book = (LoqBook) parser.getTarget().getTarget();
		Project release = book.getConcerns().findConcern(
				new BracketedString("ReleaseJUnit4.5"));

		Requirement<Project> requirement = book.getGraph().firstRequirement(
				release, GlindaTimePoint.now());
		Project prerequisite = requirement.getPrerequisite();
		assertFalse(prerequisite.foreverDone());
	}

	@RunWith(Theories.class)
	public static class SpeedUnit {
		@DataPoint
		public static OutgoingLinkFinder<Project> finder = Project.LINK_FINDER;

		@Theory
		@Test(timeout = 100)
		public void bracketedIgnoredLinkIsFast(
				OutgoingLinkFinder<Project> finder) {
			IgnoredLinks links = new IgnoredLinks() {
				@Override
				protected String descendants(
						OutgoingLinkFinder<Project> projectOutgoingLinkFinder) {
					return "ABCDEF";
				}
			};

			for (int i = 0; i < 10000; i++)
				links.bracketedDescendants(finder);
		}
	}

	@Test
	public void hardenDoesNotClearOtherRequirements() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo A C");
		r("#priorTo A D");
		r("#priorTo A E");
		r("#now 2007-01-03 12:00:00");
		r("#harden A");
		r("#done B");
		nowIs("2007-01-03 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:A");
		w("  [-/-> C]");
		w("  [-/-> D]");
		w("  [-/-> E]");
		w(" ");
		w("! p:C");
		w("! p:D");
		w("! p:E");
	}
}
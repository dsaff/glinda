/**
 * 
 */
package test.net.saff.glinda.user.b_links.b_harden;

import static com.domainlanguage.time.Duration.days;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.EnumSet;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementType;
import net.saff.glinda.projects.tasks.ChainStatus;
import net.saff.glinda.projects.tasks.Task;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class B_MovesDownCycles extends CondCorrespondentOnDiskTest {
	@Test
	public void hardenToMoveDownCycle() {
		r("#now 2007-01-01 12:00:00");
		r("#nextStep A B");
		r("#nextStep B C");
		r("#nextStep C A");
		r("#harden A");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:B [=> C => A => B!] <= A <= C <= [Broke cycle]");
		w("> p:A (next step: p:B)");
		w("> p:C (next step: p:A)");
	}

	@Test
	public void hardenIsGentleWithPriorToCycle() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo B C");
		r("#priorTo C A");
		r("#harden A");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:B [-> C -> A -> B!] <- A <- C <- [Broke cycle]");
		w("> p:A (first: p:B until 01-02 12:00)");
		w("> p:C (first: p:A until 01-02 12:00)");
	}

	@RunWith(Theories.class)
	public static class HardenUnit extends LoqBookDataPoints {
		LoqBook book = LoqBook.withDefaults();

		@Theory
		public void hardenIsGentleWithPriorToCycleUnit(GlindaTimePoint now) {
			Task a = Task.withoutListener("A");
			Task b = Task.withoutListener("B");
			Task c = Task.withoutListener("C");

			priorTo(now, a, b);
			priorTo(now, b, c);
			priorTo(now, c, a);
			book.now(now);
			book.harden(a);

			assertThat(book.getGraph().firstChild(a).getType(),
					is(RequirementType.SOFT));
		}

		@Theory
		public void hardenDoesNotRemoveParents(GlindaTimePoint now) {
			Task a = Task.withoutListener("A");
			Task b = Task.withoutListener("B");
			Task c = Task.withoutListener("C");

			priorTo(now, a, b);
			priorTo(now, b, c);
			priorTo(now, c, a);

			book.now(now);
			book.harden(a);

			assertThat(new ChainStatus(a, Project.LINK_FINDER)
					.chain(StatusRequest.asOf(now, book).pretendingFor(null)
							.withFlags(EnumSet.of(Flag.withParents))
							.makeRequest()), both(containsString("<")).and(
					not(containsString("\n"))));
		}

		private void priorTo(GlindaTimePoint now, Task a, Task b) {
			a.addLink(new Requirement<Project>(a, RequirementType.SOFT, b, now
					.until(now.plus(days(1)))), book.getGraph());
		}
	}

}
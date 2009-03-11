/**
 * 
 */
package test.net.saff.glinda.user.b_links.a_basics;

import static com.domainlanguage.time.Duration.days;
import static org.junit.Assert.assertTrue;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.tasks.RequirementIgnoredLinks;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class B_1_a_PriorTasks extends CondCorrespondentOnDiskTest {
	@Test
	public void hideParentWhileInEffect() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		nowIs("2007-01-01 12:00:00");

		runStatus();
		w("! p:CleanFloor");
		w("> p:FixFloor (first: p:CleanFloor until 2007-01-02 12:00:00)");
	}

	@Test
	public void expire() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		nowIs("2007-01-03 12:00:00");

		runStatus();
		w("! p:FixFloor [-/-> CleanFloor]");
		w("! p:CleanFloor");
	}

	@Test
	public void expireSoonerThanOneDay() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor 5 minutes");
		nowIs("2007-01-01 12:10:00");

		runStatus();
		w("! p:FixFloor [-/-> CleanFloor]");
		w("! p:CleanFloor");
	}

	@Test
	public void notEffectDeprioritizedChildren() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#priorTo CleanFloor FindMop");
		nowIs("2007-01-01 12:00:00");

		runStatus();
		w("! p:FindMop");
		w("> p:FixFloor (first: p:CleanFloor until 2007-01-02 12:00:00)");
		w("> p:CleanFloor (first: p:FindMop until 2007-01-02 12:00:00)");
	}

	@Test
	public void onlyActivateOneProjectFromChain() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#priorTo CleanFloor FindMop");
		r("#wait FindMop 1 day");
		nowIs("2007-01-01 12:00:00");

		runStatus();
		w("! p:CleanFloor [-> FindMop]");
		w("> p:FixFloor (first: p:CleanFloor until 2007-01-02 12:00:00)");
		w("> p:FindMop (waiting until 2007-01-02 12:00:00)");
	}

	@Test
	public void stillActivateIfChildIsDone() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#priorTo CleanFloor FindMop");
		r("#done FindMop");
		r("#wait CleanFloor 1 day");
		nowIs("2007-01-01 12:00:00");

		runStatus();
		w("! p:FixFloor [-> CleanFloor]");
		w("> p:CleanFloor (waiting until 2007-01-02 12:00:00)");
	}

	@Test
	public void workWithOkGoals() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#track DishesOnFloor 0");
		r("#priorTo FixFloor DishesOnFloor");
		nowIs("2007-01-01 12:10:00");

		runStatus();
		w("! p:FixFloor [-> DishesOnFloor]");
		w("> DishesOnFloor (was null, is 0)");
	}

	@Test
	public void workWithGoalsPriorToOthers() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#startGoal DishesOnFloor");
		r("#priorTo DishesOnFloor FixFloor");
		nowIs("2007-01-01 12:10:00");

		runStatus();
		w("! p:FixFloor");
		w("> DishesOnFloor (first: p:FixFloor until 2007-01-02 12:00:00)");
	}

	@Test
	public void showIgnoredLinkWhenVelocityGoalIsBehind() {
		addImport(Goal.class, "set");
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#track DishesOnFloor 1");
		r("#set DishesOnFloor moreIsBetter=true");
		r("#set DishesOnFloor compareVelocity=true");
		r("#set DishesOnFloor comparisonTimeframe=1 day");
		r("#priorTo DishesOnFloor FixFloor");
		r("#now 2007-01-02 12:00:00");
		r("#wait FixFloor 1 day");
		nowIs("2007-01-02 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("! DishesOnFloor [-> FixFloor] (was 1.00/day, needs 1.0 since 01-01 12:00)");
		w("> p:FixFloor (waiting until 2007-01-03 12:00:00)");
	}

	private void runStatus() {
		run("status", "-actionItemsFirst", "-showFullTimes");
		w("status:");
	}

	@Test
	public void showExpiredLink() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");

		nowIs("2007-01-03 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:A [-/-> B]");
		w("! p:B");
	}

	@RunWith(Theories.class)
	public static class ExpiredLinkUnit extends LoqBookDataPoints {
		@Theory
		public void recognizeSomethingIsIgnored(GlindaTimePoint now) {
			LoqBook book = LoqBook.withDefaults();
			book.now(now);
			Project a = book.startProject(new BracketedString("A"));
			Project b = book.startProject(new BracketedString("B"));
			book.priorTo(a, b, days(1));
			book.setNow(now.plus(days(2)));

			Requirement<Project> each = book.getGraph().getChildren(a)
					.iterator().next();
			StatusRequest request = book.context(Flag.NONE);
			assertTrue(new RequirementIgnoredLinks(each, request)
					.somethingIsIgnored(Project.LINK_FINDER));
		}
	}
}
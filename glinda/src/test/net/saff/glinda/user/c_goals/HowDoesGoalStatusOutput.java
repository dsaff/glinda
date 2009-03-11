/**
 * 
 */
package test.net.saff.glinda.user.c_goals;

import static com.domainlanguage.time.Duration.days;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import net.saff.glinda.book.DurationWaitTime;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.time.GlindaTimePointParser;

import org.junit.Before;
import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class HowDoesGoalStatusOutput extends CondCorrespondentOnDiskTest {
	@Before
	public void addImport() {
		addImport(Goal.class, "set");
	}

	@Test
	public void indicateAGoalThatsBehind() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms 1");
		r("#NOW 01-02 12:00");
		r("#track MessyRooms 2");
		super.runAtTime("01-02 12:01", "goalStatus", "MessyRooms");
		w("goalStatus: ! MessyRooms (was 1, is 2)");
	}

	@Test
	public void displayIfTheresBeenNoTrackSinceYesterday() {
		r("#NOW 07-01 12:00");
		r("#track MessyRooms 3");
		super.runAtTime("07-05 12:00", "goalStatus", "MessyRooms");
		w("goalStatus: ! MessyRooms (4 days since last track)");
	}

	@Test
	public void displayWhenGoalIsWaiting() {
		r("#NOW 2007-01-01 12:00:00");
		r("#track MessyRooms 1");
		r("#NOW 2007-01-02 12:00:00");
		r("#track MessyRooms 2");
		r("#wait MessyRooms 1 hour");
		super.runAtTime("01-02 12:01", "goalStatus", "MessyRooms");
		w("goalStatus: > MessyRooms (waiting until 2007-01-02 13:00:00)");
	}

	@Test
	public void changeWhenMoreIsBetter() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms 1");
		r("#NOW 01-02 12:00");
		r("#track MessyRooms 2");
		r("#set MessyRooms moreIsBetter=true");
		super.runAtTime("01-02 12:01", "goalStatus", "MessyRooms");
		w("goalStatus: > MessyRooms (was 1, is 2)");
	}

	@Test
	public void acceptRealNumberInput() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms: 1");
		r("#NOW 01-02 12:00");
		r("#track MessyRooms 1.5");
		super.runAtTime("01-02 12:01", "goalStatus", "MessyRooms");
		w("goalStatus: ! MessyRooms (was 1, is 1.5)");
	}

	@Test
	public void compareVelocity() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 1");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 2");
		r("#NOW 01-03 12:00");
		r("#track WidgetsBuilt 3");
		r("#set WidgetsBuilt compareVelocity=true");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		super.runAtTime("01-03 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: > WidgetsBuilt (was 1.50/day, is 2.00/day)");
	}

	@Test
	public void displayAGoalWithZeroTracks() {
		r("#NOW 01-01 12:00");
		r("#startGoal WidgetsBuilt");
		super.runAtTime("01-03 12:01", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was null, is null)");
	}

	@Test
	public void displayAGoalWithZeroTracksWhenMoreIsBetter() {
		r("#NOW 01-01 12:00");
		r("#startGoal WidgetsBuilt");
		r("#set WidgetsBuilt moreIsBetter=true");
		super.runAtTime("01-03 12:01", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was null, is null)");
	}

	@Test
	public void compareVelocityWhenBehind() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 0");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 2");
		r("#set WidgetsBuilt compareVelocity=true");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		super.runAtTime("01-03 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was 1.00/day, needs 1.0 since 01-02 12:00)");
	}

	@Test
	public void avoidBuggingAboutVelocityShortfallsThatCanBeHandledTomorrow() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 0");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt .2");
		r("#set WidgetsBuilt compareVelocity=true");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		super.runAtTime("01-03 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: > WidgetsBuilt (was 0.10/day, needs 0.1 now, needs 1.0 by 01-12 12:00)");
	}

	@Test
	public void eventuallyBugAboutGoalsThatOnlyGetDoneEveryCoupleDays() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 1");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 2");
		r("#set WidgetsBuilt compareVelocity=true");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		super.runAtTime("01-05 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was 1.00/day, needs 2.0 since 01-02 12:00)");
	}

	@Test
	public void allowSamenessWhenMoreIsBetter() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 0");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 0");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		super.runAtTime("01-02 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: > WidgetsBuilt (was 0, is 0)");
	}

	@Test
	public void gripeAboutLessWhenMoreIsBetter() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 1");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 0");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		super.runAtTime("01-02 12:01", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was 1, is 0)");
	}

	@Test
	public void lookAtHistoryWhenComparing() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 0");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 1");
		r("#NOW 01-08 12:00");
		r("#track WidgetsBuilt 1");
		super.runAtTime("01-08 12:01", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was 0 (7 days ago), is 1)");
	}

	@Test
	public void lookAtMonthAgoHistoryWhenComparing() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 0");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 1");
		r("#NOW 02-01 12:00");
		r("#track WidgetsBuilt 1");
		super.runAtTime("02-01 12:01", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was 0 (1 month ago), is 1)");
	}

	@Test
	public void lookAtHistoryWhenComparingVelocities() {
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 1");
		r("#set WidgetsBuilt compareVelocity=true");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=1 day");
		r("#NOW 01-08 12:00");
		r("#track WidgetsBuilt 1");
		r("#NOW 01-09 12:00");
		super.runAtTime("01-09 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt (was 1.00/day (7 days ago), needs 6.0 since 01-08 12:00)");
	}

	@Test
	public void showIgnoredLink() {
		r("#NOW 01-01 12:00");
		r("#track WidgetsBuilt 0");
		r("#NOW 01-02 12:00");
		r("#track WidgetsBuilt 2");
		r("#set WidgetsBuilt compareVelocity=true");
		r("#set WidgetsBuilt moreIsBetter=true");
		r("#set WidgetsBuilt comparisonTimeframe=24 hours");
		r("#priorTo WidgetsBuilt WidgetFactoryBuilt");
		r("#wait WidgetFactoryBuilt 30 days");
		super.runAtTime("01-03 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt [-> WidgetFactoryBuilt] (was 1.00/day, needs 1.0 since 01-02 12:00)");
	}

	@Test
	public void showIgnoredLinkWhenUntracked() {
		r("#NOW 01-01 12:00");
		r("#startGoal WidgetsBuilt");
		r("#priorTo WidgetsBuilt WidgetFactoryBuilt");
		r("#wait WidgetFactoryBuilt 30 days");
		super.runAtTime("01-02 12:00", "goalStatus", "WidgetsBuilt");
		w("goalStatus: ! WidgetsBuilt [-> WidgetFactoryBuilt] (was null, is null)");
	}

	@Test
	public void showIgnoredLinkWhenUntrackedUnitized() {
		LoqBook book = LoqBook.withDefaults();
		book.now(new GlindaTimePointParser().parse("01-01 12:00"));
		Goal widgets = book.getConcerns().startGoal(
				new BracketedString("WidgetsBuilt"));
		Project factory = book.startProject(new BracketedString(
				"WidgetFactoryBuilt"));
		book.priorTo(widgets, factory, null);
		book.wait(factory, new DurationWaitTime(days(30)));
		book.setNow(new GlindaTimePointParser().parse("01-02 12:00"));
		assertThat(
				book.requestor().statusLine(widgets),
				is("! WidgetsBuilt [-> WidgetFactoryBuilt] (was null, is null)"));
	}
}

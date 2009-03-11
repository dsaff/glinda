package test.net.saff.glinda.units.projects.goals;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.hours;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static net.saff.glinda.time.comparison.AbsoluteTimeComparisonMatcher.before;
import static net.saff.glinda.time.comparison.DurationComparison.atLeast;
import static net.saff.glinda.time.comparison.DurationComparison.atMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

import java.util.EnumSet;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.projects.goals.GoalValue;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

import com.domainlanguage.time.Duration;

@RunWith(Theories.class)
public class HowDoesFreshGoal extends LoqBookDataPoints {
	private final Goal goal;

	public HowDoesFreshGoal(Goal goal) {
		assumeTrue(goal.isFresh());
		assumeTrue(!goal.getSettings().isCompareVelocity());
		this.goal = goal;
	}

	@Theory
	public void indicateAGoodGoalStatus(GlindaTimePoint now, int valueOne,
			int valueTwo) {
		assumeTrue(valueTwo <= valueOne);
		goal.track(goal.comparedTimepoint(now), valueOne);
		goal.track(goal.comparedTimepoint(now).minus(hours(1)), valueTwo);

		assertThat(statusLine(now), not(containsString("!")));
	}

	@Theory
	public void rateStatusOkIfYoungerThanADay(GlindaTimePoint now, int value) {
		goal.track(now, value);
		assertThat(statusLine(now), not(containsString("!")));
	}

	@Theory
	public void showStatusWhenTrackWasTooLongAgo(GlindaTimePoint now, int n,
			int value) {
		assumeTrue(n > 1);
		goal.track(now.minus(Duration.days(n)), value);
		assertThat(statusLine(now), containsStrings(goal, n));
	}

	@Theory
	public void showStatusWhenTrackWasTooLongAgoWithDesiredTimeFrame(
			GlindaTimePoint now, Duration d, int value) {
		assumeTrue(d.compareTo(Duration.NONE) > 0);
		goal.getSettings().setComparisonTimeframe(d);
		goal.track(now.minus(d.plus(d)), value);
		assertThat(statusLine(now), containsStrings(StatusPrefix.NOT_OK));
	}

	@Theory
	public void stopWaiting(GlindaTimePoint now, int value) {
		goal.track(now.minus(hours(2)), value);
		goal.setWaitTime(now.minus(hours(1)));
		assertThat(statusLine(now), not(containsString("waiting")));
	}

	@Theory
	public void showStatusWithTimes(GlindaTimePoint now, double valueOne,
			double valueTwo) {
		goal.track(now.minus(days(1)), valueOne);
		goal.track(now, valueTwo);
		StatusRequest request = StatusRequest.createStatusRequest(now, null, EnumSet.of(Flag.withTimes));
		StatusRequestor r = new StatusRequestor(request);
		Status<Project> status = r.status(goal);
		String line = request.format(
				goal.nameForStatusLine(request.getFlags()), status);

		assertThat(request.format("a", goal.activeStatus(request, null)),
				containsString(GoalValue.absolute(valueOne)));
		assertThat(line, containsStrings(goal, GoalValue.absolute(valueOne)));
	}

	@Theory
	public void persistTrackedValues(GlindaTimePoint now, double value) {
		goal.track(goal.comparedTimepoint(now).minus(hours(1)), value);
		assertThat(goal.yesterdaysDouble(now), is(value));
	}

	@Theory
	public void persistMultipleTrackedValues(GlindaTimePoint now,
			double valueOne, double valueTwo) {
		goal.track(goal.comparedTimepoint(now), valueOne);
		goal.track(goal.comparedTimepoint(now).plus(hours(1)), valueTwo);

		assertThat(goal.yesterdaysDouble(now), is(valueOne));
		assertThat(goal.yesterdaysDouble(now.plus(hours(1))), is(valueTwo));
	}

	@Theory
	public void compareVelocities(GlindaTimePoint now, double value) {
		goal.track(now.minus(days(2)), value);
		goal.track(now.minus(days(1)), value);
		goal.track(now, value);
		shouldIncreaseVelocityEvery24hours();

		assertThat(statusLine(now), containsStrings(goal, value));
	}

	@Theory
	public void includeAllDataInStatus(GlindaTimePoint firstTrack,
			GlindaTimePoint secondTrack, GlindaTimePoint now, int firstValue,
			int secondValue) {
		Duration timeframe = goal.getDefaultComparisonTimeframe();
		assumeThat(firstTrack, atLeast(timeframe).before(now));
		assumeThat(secondTrack, atMost(timeframe).before(now));
		assumeThat(firstTrack, before(secondTrack));
		assumeThat(now, not(before(secondTrack)));

		goal.track(firstTrack, firstValue);
		goal.track(secondTrack, secondValue);

		assertThat(statusLine(now), containsStrings(goal, firstValue,
				secondValue));
	}

	@Theory
	public void showVelocityDeficit(GlindaTimePoint now, double recentEffort,
			double shortfall) {
		assumeTrue(shortfall > 0);
		goal.track(now.minus(hours(24)), recentEffort + shortfall);
		GlindaTimePoint halfway = now.minus(hours(12));
		goal.track(halfway, recentEffort);

		shouldIncreaseVelocityEvery24hours();

		assertThat(statusLine(now), containsStrings(shortfall, halfway
				.displayRelativeTo(now)));
	}

	@Theory
	public void pullUpStatusOnAGoalWithoutTracks() {
		new StatusRequestor(StatusRequest.createStatusRequest(GlindaTimePoint.now(), null, Flag.NONE)).statusLine(goal);
	}

	@Theory
	public void determineIfAVelocityAdjustmentCanBePostponed(
			int daysUntilNeeded, GlindaTimePoint now) {
		assumeTrue(daysUntilNeeded > 2);

		goal.track(now.minus(days((daysUntilNeeded + 1) * 2 - 1)), 1);
		goal.track(now.minus(days(1)), 1);
		shouldIncreaseVelocityEvery24hours();

		assertThat(statusLine(now), containsStrings(now.plus(
				days(daysUntilNeeded)).displayRelativeTo(now)));
	}

	private String statusLine(GlindaTimePoint now) {
		return new StatusRequestor(StatusRequest.createStatusRequest(now, null, Flag.NONE))
				.statusLine(goal);
	}

	private void shouldIncreaseVelocityEvery24hours() {
		goal.getSettings().setComparisonTimeframe(hours(24));
		goal.getSettings().setCompareVelocity(true);
		goal.getSettings().setMoreIsBetter(true);
	}
}
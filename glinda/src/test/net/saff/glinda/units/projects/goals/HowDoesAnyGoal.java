package test.net.saff.glinda.units.projects.goals;

import static com.domainlanguage.time.Duration.hours;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesAnyGoal extends LoqBookDataPoints {
	private Goal goal;

	public HowDoesAnyGoal(Goal goal) {
		this.goal = goal;
	}

	@Theory
	public void indicateThereAreNoTracks(GlindaTimePoint now, double value) {
		goal.track(now, value);
		assertFalse(goal.isFresh());
	}

	@Theory
	public void includeGoalNameWhenWaiting(GlindaTimePoint time) {
		goal.setWaitTime(time.plus(hours(1)));
		assertThat(status(time), containsStrings(goal));
	}

	@Theory
	public void showStatusWhenPaused(GlindaTimePoint now) {
		goal.pause();
		assertThat(status(now), containsStrings(goal, "z"));
	}

	@Theory
	public void resume(GlindaTimePoint now) {
		goal.resume();
		assertFalse(status(now).startsWith("z"));
	}

	private String status(GlindaTimePoint now) {
		// TODO: DUP
		return new StatusRequestor(StatusRequest.asOf(now, new RequirementGraph<Project>()).pretendingFor(null).withFlags(Flag.NONE)
		.makeRequest()).statusLine(goal);
	}
}
package test.net.saff.glinda.units.projects;

import static com.domainlanguage.time.Duration.days;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.RequirementType;
import net.saff.glinda.projects.tasks.ActivityRequestor;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

import java.util.EnumSet;

@RunWith(Theories.class)
public class HowDoesProject extends LoqBookDataPoints {
	private final GlindaTimePoint now;
	private RequirementGraph<Project> graph = new RequirementGraph<Project>();

	@DataPoint
	public static StatusRequest request() {
		// TODO: pass-through
		return StatusRequest.asOf(GlindaTimePoint.now(),
				new RequirementGraph<Project>()).pretendingFor(null).withFlags(
				Flag.NONE).makeRequest();
	}

	public HowDoesProject(GlindaTimePoint now) {
		this.now = now;
	}

	@Theory
	public void showNextStepInStatusLine(Project project, Project blocker) {
		assumeTrue(isActionable(project));
		addHardLink(project, blocker, graph);
		assertThat(new StatusRequestor(StatusRequest.asOf(
				GlindaTimePoint.now(), graph).pretendingFor(null).withFlags(
				Flag.NONE).makeRequest()).statusLine(project), containsStrings(
				project, blocker));
	}

	@Theory
	public void displayParents(Project project, Project nextStepProject) {
		assumeTrue(isActionable(project));
		assumeTrue(isActionable(nextStepProject));

		addHardLink(project, nextStepProject, graph);
		StatusRequest request = StatusRequest.asOf(now, graph).pretendingFor(
				null).withFlags(EnumSet.of(Flag.withParents)).makeRequest();
		assertThat(new StatusRequestor(request).statusLine(nextStepProject),
				containsStrings(project, nextStepProject));
	}

	private void addHardLink(Project project, Project blocker,
			RequirementGraph<Project> graph) {
		// TODO (Jun 16, 2008 6:14:38 PM): pass-through
		project.addLink(hardForever(project, blocker), graph);
	}

	private Requirement<Project> hardForever(Project blocked, Project blocker) {
		return Requirement.hardForever(blocked, blocker, now);
	}

	@Theory
	public void becomeActionableWhenChildIsDone(Project a, Project b) {
		assumeTrue(isActionable(a));
		a.addRequirement(hardForever(a, b), graph);
		b.done(now, graph);
		assertTrue(isActionable(a));
	}

	private boolean isActionable(Project a) {
		return new ActivityRequestor(requestor().getStatusRequest())
				.isActionable(a);
	}

	@Theory
	public void showSoftLinkInStatus(Project a, Project b) {
		assumeTrue(isActionable(a));
		a.addLink(new Requirement<Project>(a, RequirementType.SOFT, b, now
				.until(now.plus(days(1)))), graph);
		assertThat(requestor().statusLine(a), containsString(b.toString()));
	}

	private StatusRequestor requestor() {
		return new StatusRequestor(StatusRequest.asOf(now, graph)
				.pretendingFor(null).withFlags(Flag.NONE).makeRequest());
	}
}

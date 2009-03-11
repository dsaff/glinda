package test.net.saff.glinda.units.projects;

import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.tasks.BlockedAncestry;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesBlockedAncestry extends LoqBookDataPoints {
	@DataPoint
	public static Requirement<Project> HARD = Requirement.hardForever(null,
			freshGoal(), now);

	@Theory
	public void computeDependency(Project project, Requirement<Project> r) {
		GlindaTimePoint time = r.getCreationTime();
		// TODO: DUP
		StatusRequest request = StatusRequest.asOf(time, new RequirementGraph<Project>()).pretendingFor(null).withFlags(EnumSet.of(Flag.withParents))
		.makeRequest();
		project.addLink(r, request.getGraph());
		BlockedAncestry ancestry = new BlockedAncestry(request).with(r
				.getPrerequisite(time));
		assertTrue(ancestry.directlyDependsOnThis(request, project));
	}
}

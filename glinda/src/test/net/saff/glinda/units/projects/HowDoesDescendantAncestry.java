package test.net.saff.glinda.units.projects;

import java.util.List;

import net.saff.glinda.projects.core.DescendantAncestry;
import net.saff.glinda.projects.core.OutgoingLinkFinder;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.tasks.Task;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesDescendantAncestry extends LoqBookDataPoints {
	@Theory
	public void handleNullRequirementsReturns(GlindaTimePoint now) {
		new DescendantAncestry(now, Requirement.hardForever(null,
				((Project) Task.withoutListener("A")), now),
				new OutgoingLinkFinder<Project>() {
					public List<Requirement<Project>> getRequirements(
							Project node, RequirementGraph<Project> graph) {
						return null;
					}
				}).descendants(new RequirementGraph<Project>());
	}
}

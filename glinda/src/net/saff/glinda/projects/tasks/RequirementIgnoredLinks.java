package net.saff.glinda.projects.tasks;

import java.util.ArrayList;

import net.saff.glinda.projects.core.DescendantAncestry;
import net.saff.glinda.projects.core.OutgoingLinkFinder;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Cycle;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.time.GlindaTimePoint;

public class RequirementIgnoredLinks extends IgnoredLinks {
	private Requirement<Project> requirement;
	private StatusInstant<Project> instant;

	public RequirementIgnoredLinks(Requirement<Project> requirement,
			StatusInstant<Project> instant2) {
		this.requirement = requirement;
		this.instant = instant2;
	}

	public Requirement<Project> getRequirement() {
		return requirement;
	}

	public StatusInstant<Project> getInstant() {
		return instant;
	}

	@Override
	protected String descendants(
			OutgoingLinkFinder<Project> projectOutgoingLinkFinder) {
		Requirement<Project> each = getRequirement();
		StatusInstant<Project> instant = getInstant();
		GlindaTimePoint now = instant.getNow();
		return new DescendantAncestry(now, each, projectOutgoingLinkFinder
				.possiblyReplaceWithCycle(this
						.findCycle(projectOutgoingLinkFinder), now))
				.descendants(instant.getGraph()).trim();
	}

	public Cycle<Requirement<Project>> findCycle(OutgoingLinkFinder<Project> finder) {
		return new DescendantAncestry(getInstant().getNow(), getRequirement(),
				finder).findCycle(getInstant());
	}

	public boolean somethingIsIgnored(OutgoingLinkFinder<Project> finder) {
		return findCycle(finder) != null
				|| !getRequirement().isDone(getInstant().getNow());
	}

	void addIfPresent(ArrayList<String> ignoredLinks,
			OutgoingLinkFinder<Project> finder) {
		if (somethingIsIgnored(finder))
			ignoredLinks.add(bracketedDescendants(finder));
	}
}
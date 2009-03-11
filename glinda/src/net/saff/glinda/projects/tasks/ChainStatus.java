/**
 * 
 */
package net.saff.glinda.projects.tasks;

import java.util.ArrayList;
import java.util.Collection;

import net.saff.glinda.projects.core.OutgoingLinkFinder;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.ConcreteStatus;
import net.saff.glinda.projects.status.StatusPrefix;

public class ChainStatus extends ConcreteStatus<Project> {
	private final Project project;
	private final OutgoingLinkFinder<Project> finder;

	public ChainStatus(Project project, OutgoingLinkFinder<Project> outgoingLinkFinder) {
		super(StatusPrefix.NOT_OK);
		this.project = project;
		this.finder = outgoingLinkFinder;
	}

	@Override
	public String[] getComments(StatusInstant<Project> instant) {
		return new String[] { chain(instant) };
	}

	public String chain(StatusInstant<Project> instant) {
		String ignoredLink = ignoredLink(instant);
		String parents = possiblyMultipleParents(instant);

		if (ignoredLink.contains("\n"))
			return ignoredLink + "\n " + parents;
		return ignoredLink + parents;
	}

	private Collection<Requirement<Project>> getRequirements(RequirementGraph<Project> graph) {
		return finder.getRequirements(project, graph);
	}

	public String ignoredLink(StatusInstant<Project> instant) {
		ArrayList<String> ignoredLinks = ignoredLinks(instant);
		if (ignoredLinks.size() == 0)
			return "";
		if (ignoredLinks.size() == 1)
			return ignoredLinks.get(0);
		return joinWithNewLines(ignoredLinks);
	}

	public String joinWithNewLines(ArrayList<String> ignoredLinks) {
		String links = "";
		for (String link : ignoredLinks)
			links += "\n " + link;
		return links;
	}

	public ArrayList<String> ignoredLinks(StatusInstant<Project> instant) {
		ArrayList<String> ignoredLinks = new ArrayList<String>();
		for (Requirement<Project> each : getRequirements(instant.getGraph()))
			new RequirementIgnoredLinks(each, instant).addIfPresent(ignoredLinks, finder);
		return ignoredLinks;
	}

	public String possiblyMultipleParents(StatusInstant<Project> instant) {
		if (!instant.getFlags().contains(Flag.withParents))
			return "";

		return branchingRule().parents(
				new BlockedAncestry(instant).with(project));
	}

	public ChainingRule branchingRule() {
		if (project.shouldShowBranchedParents())
			return ChainingRule.branching();
		return ChainingRule.noBranching();
	}
}
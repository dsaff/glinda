package net.saff.glinda.projects.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.saff.glinda.projects.requirement.Cycle;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.time.GlindaTimePoint;

public class DescendantAncestry {
	private final Requirement<Project> requirement;
	private final DescendantAncestry next;
	private final GlindaTimePoint now;
	private final OutgoingLinkFinder<Project> finder;

	public DescendantAncestry(GlindaTimePoint now,
			Requirement<Project> requirement, OutgoingLinkFinder<Project> finder) {
		this(now, requirement, finder, null);
	}

	public DescendantAncestry(GlindaTimePoint now,
			Requirement<Project> requirement,
			OutgoingLinkFinder<Project> finder, DescendantAncestry next) {
		this.now = now;
		this.requirement = requirement;
		this.finder = finder;
		this.next = next;
	}

	public String descendants(RequirementGraph<Project> graph) {
		if (next != null && next.contains(requirement))
			return "!";
		if (requirement == null)
			return "";
		Project prerequisite = getPrerequsiteEvenIfExpired();
		if (prerequisite == null)
			return "";
		Collection<Requirement<Project>> requirements = finder
				.getRequirements(prerequisite, graph);
		return " "
				+ requirement.rightArrow(now)
				+ " "
				+ prerequisite
				+ new DescendantAncestry(now, first(requirements), finder, this)
						.descendants(graph);
	}

	private Project getPrerequsiteEvenIfExpired() {
		return requirement.getPrerequisiteEvenIfExpired(now);
	}

	private boolean contains(Requirement<Project> contained) {
		if (requirement.equals(contained))
			return true;
		if (next == null)
			return false;
		return next.contains(contained);
	}

	private Requirement<Project> first(
			Collection<Requirement<Project>> requirements) {
		if (requirements == null)
			return null;
		if (requirements.isEmpty())
			return null;
		return requirements.iterator().next();
	}

	private Project getPrerequisite(Requirement<Project> requirement2) {
		if (requirement2 == null)
			return null;
		return requirement2.getPrerequisite(now);
	}

	public Cycle<Requirement<Project>> findCycle(
			StatusInstant<Project> statusInstant) {
		Map<Object, Cycle<Requirement<Project>>> cycleCache = statusInstant
				.getCycleCache();
		if (!cycleCache.containsKey(requirement))
			cycleCache.put(requirement, computeCycle(statusInstant));
		return cycleCache.get(requirement);
	}

	private Cycle<Requirement<Project>> computeCycle(
			StatusInstant<Project> statusInstant) {
		if (getPrerequisite(requirement) == null)
			return null;
		if (next != null && next.contains(requirement))
			return removePrecedingElements(next.toList());

		return cycleFromOutgoingRequirements(statusInstant);
	}

	private Cycle<Requirement<Project>> cycleFromOutgoingRequirements(
			StatusInstant<Project> statusInstant) {
		for (Requirement<Project> blocker : finder.getRequirements(
				getPrerequisite(requirement), statusInstant.getGraph())) {
			RequirementNode<Project> prerequisite = getPrerequisite(blocker);
			if (prerequisite != null
					&& !prerequisite.intrinsicallyInactionable(statusInstant)) {
				Cycle<Requirement<Project>> cycle = new DescendantAncestry(now,
						blocker, finder, this).findCycle(statusInstant);
				if (cycle != null)
					return cycle;
			}
		}
		return null;
	}

	private Cycle<Requirement<Project>> removePrecedingElements(
			List<Requirement<Project>> list) {
		while (!list.get(0).equals(requirement))
			list.remove(0);
		return new Cycle<Requirement<Project>>(list);
	}

	private List<Requirement<Project>> toList() {
		List<Requirement<Project>> list = new ArrayList<Requirement<Project>>();
		addAllToList(list);
		return list;
	}

	private void addAllToList(List<Requirement<Project>> list) {
		if (next != null)
			next.addAllToList(list);
		list.add(requirement);
	}

	public void harden(Project project, StatusInstant<Project> instant) {
		project.harden(findCycle(instant) != null, now, instant.getGraph());
	}

	public static DescendantAncestry fromProject(Project project,
			GlindaTimePoint now, RequirementGraph<Project> graph) {
		// TODO: pass-through
		return new DescendantAncestry(now,
				graph.firstRequirement(project, now), Project.LINK_FINDER);
	}
}

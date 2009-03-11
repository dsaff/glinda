package net.saff.glinda.projects.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.ActionableCache;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.ConcreteStatus;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.time.GlindaTimePoint;

public class ActivityRequestor extends ArrayList<Project> {
	private static final long serialVersionUID = 1L;
	private final StatusInstant<Project> statusRequest;

	public ActivityRequestor(StatusInstant<Project> instant2, List<Project> list) {
		super(list);
		this.statusRequest = instant2;
	}

	public ActivityRequestor(StatusInstant<Project> instant2) {
		this(instant2, Collections.<Project> emptyList());
	}

	public ActivityRequestor plus(Project prerequisite) {
		ActivityRequestor list = new ActivityRequestor(getStatusRequest(), this);
		list.add(prerequisite);
		return list;
	}

	public boolean isActionable(Project r) {
		if (!isCached(r))
			cache(r);
		return getCached(r);
	}

	public Boolean cache(Project r) {
		return actionableCache().put(r, computeActionable(r));
	}

	public boolean isCached(Project r) {
		return actionableCache().containsKey(r);
	}

	public Boolean getCached(Project r) {
		return actionableCache().get(r);
	}

	public ActionableCache actionableCache() {
		return getStatusRequest().getActionableCache();
	}

	public boolean computeActionable(Project p) {
		return !intrinsicallyInactionable(p)
				&& !isTripWithOnlyBlockedParents(p)
				&& getBlockingRequirement(p) == null;
	}

	public boolean isTripWithOnlyBlockedParents(Project p) {
		ActionableCache cache = statusRequest.getActionableCache();
		// TODO: envious
		Boolean oldValue = cache.get(p);
		try {
			cache.put(p, true);
			return new BlockedAncestry(statusRequest).with(p)
					.isTripWithOnlyBlockedParents(p);
		} finally {
			if (oldValue == null)
				cache.remove(p);
			else
				cache.put(p, oldValue);
		}
	}

	public List<Requirement<Project>> getBlockingRequirements(Project p) {
		List<Requirement<Project>> requirements = new ArrayList<Requirement<Project>>();

		for (Requirement<Project> requirement : getGraph().getChildren(p))
			if (isBlocking(requirement))
				requirements.add(requirement);
		return requirements;
	}

	public Requirement<Project> getBlockingRequirement(Project p) {
		List<Requirement<Project>> requirements = getBlockingRequirements(p);
		return requirements.isEmpty() ? null : requirements.get(0);
	}

	public boolean isBlocking(Requirement<? extends Project> requirement) {
		return !isExpiredOrDone(requirement)
				&& (hasBlockingDescendant(requirement) || requirement
						.alwaysBlocksParent());
	}

	public boolean isExpiredOrDone(Requirement<? extends Project> requirement) {
		return requirement.isExpiredOrDone(getNow());
	}

	public GlindaTimePoint getNow() {
		return getStatusRequest().getNow();
	}

	public boolean hasBlockingDescendant(
			Requirement<? extends Project> requirement) {
		Project prerequisite = requirement.getPrerequisite(getNow());
		if (prerequisite == null)
			return false;

		if (intrinsicallyInactionable((Project) prerequisite))
			return false;
		if (contains(prerequisite))
			return false;
		if (requirement.isExpiredOrDone(getNow()))
			return false;

		ActivityRequestor newLineage = plus(prerequisite);
		if (newLineage.isActionable(prerequisite))
			return true;

		for (Requirement<? extends Project> each : getGraph().getChildren(
				prerequisite))
			if (newLineage.hasBlockingDescendant(each))
				return true;
		return false;
	}

	public RequirementGraph<Project> getGraph() {
		return statusRequest.getGraph();
	}

	public boolean intrinsicallyInactionable(Project p) {
		return p.intrinsicallyInactionable(getStatusRequest());
	}

	public Status<Project> blockedStatus(Project project) {
		Requirement<Project> blockingRequirement = getBlockingRequirement(project);
		if (blockingRequirement != null)
			return blockedByRequirement(getStatusRequest(), blockingRequirement);
		if (isTripWithOnlyBlockedParents(project))
			return blockedParentStatus(project);
		if (getStatusRequest().isPretenseReason(project))
			return new ConcreteStatus<Project>(StatusPrefix.OK,
					"currently pretending");
		// TODO: not tested!
		return project.blockedNotByRequirementStatus(getStatusRequest());
	}

	public ConcreteStatus<Project> blockedByRequirement(
			StatusInstant<Project> instant,
			Requirement<Project> blockingRequirement) {
		return new ConcreteStatus<Project>(StatusPrefix.OK, blockingRequirement
				.blockedComment(instant));
	}

	public ConcreteStatus<Project> blockedParentStatus(Project project) {
		return new ConcreteStatus<Project>(StatusPrefix.OK,
				"waiting on parent: "
						+ project.getBlocked(getGraph()).iterator().next());
	}

	private StatusInstant<Project> getStatusRequest() {
		return statusRequest;
	}
}

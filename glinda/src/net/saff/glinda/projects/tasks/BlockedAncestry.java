/**
 * 
 */
package net.saff.glinda.projects.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;

public class BlockedAncestry {
	private RequirementNode<Project> current;
	private final List<RequirementNode<?>> lineage;
	private boolean shouldStar = false;
	private final StatusInstant<Project> instant;

	public BlockedAncestry(StatusInstant<Project> instant2) {
		this(null, new ArrayList<RequirementNode<?>>(), instant2);
	}

	public BlockedAncestry(RequirementNode<Project> project,
			List<RequirementNode<?>> lineage2, StatusInstant<Project> instant2) {
		this.current = project;
		this.lineage = lineage2;
		this.instant = instant2;
	}

	public String leftArrowed(String lastName, String nextName) {
		return " " + current.leftArrowString(lastName, instant.getGraph())
				+ star() + " " + nextName;
	}

	private String star() {
		if (shouldStar)
			return "*";
		return "";
	}

	public Collection<Project> getDirectlyBlocked() {
		ArrayList<Project> reallyBlocked = new ArrayList<Project>();
		for (Project each : getAllBlocked())
			if (!each.intrinsicallyInactionable(getInstant())
					&& directlyDependsOnThis(getInstant(), each))
				reallyBlocked.add(each);
		return reallyBlocked;
	}

	public boolean directlyDependsOnThis(StatusInstant<Project> instant,
			Project project) {
		List<Requirement<Project>> blocking = new ActivityRequestor(instant)
				.getBlockingRequirements(project);

		return !(blocking.isEmpty()) && currentIsLastPrereq(instant, blocking);
	}

	public boolean currentIsLastPrereq(StatusInstant<Project> instant,
			List<Requirement<Project>> blocking) {
		return blocking.get(blocking.size() - 1).getPrerequisite(
				instant.getNow()).equals(current);
	}

	public Collection<Project> getAllBlocked() {
		return current.getBlocked(instant.getGraph());
	}

	public BlockedAncestry with(RequirementNode<Project> blockedItem) {
		if (current != null)
			lineage.add(current);
		return new BlockedAncestry(blockedItem, lineage, getInstant());
	}

	public boolean isCycle() {
		return lineage.contains(current);
	}

	public String leftArrowed(String lastName) {
		return leftArrowed(lastName, currentName());
	}

	public String currentName() {
		return current.toString();
	}

	public BlockedAncestry onlyParent() {
		return with(getDirectlyBlocked().iterator().next());
	}

	public BlockedAncestry firstParentOfMany() {
		return onlyParent().addStar();
	}

	private BlockedAncestry addStar() {
		shouldStar = true;
		return this;
	}

	public String lineageTop() {
		return lineage.get(lineage.size() - 1).toString();
	}

	public StatusInstant<Project> getInstant() {
		return instant;
	}

	public boolean isTripWithOnlyBlockedParents(Project project) {
		return project.shouldShowBranchedParents()
				&& !getAllBlocked().isEmpty() && getDirectlyBlocked().isEmpty();
	}
}
/**
 * 
 */
package net.saff.glinda.projects.display;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.tasks.ActivityRequestor;

public class BlockedDeterminant extends ProjectStatusDeterminant {
	private final Project project;

	public BlockedDeterminant(Project project) {
		this.project = project;
	}

	@Override
	public boolean canBeApplied(StatusInstant<Project> statusRequest) {
		return !new ActivityRequestor(statusRequest).isActionable(getProject());
	}

	@Override
	public Status<Project> status(StatusInstant<Project> instant) {
		return new ActivityRequestor(instant).blockedStatus(getProject());
	}

	public Project getProject() {
		return project;
	}
}

/**
 * 
 */
package net.saff.glinda.projects.display;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.tasks.ChainStatus;

public class ActiveStatusDeterminant extends ProjectStatusDeterminant {
	private final Project project;

	ActiveStatusDeterminant(Project project) {
		this.project = project;
	}

	@Override
	public Status<Project> status(StatusInstant<Project> instant) {
		return project.activeStatus(instant, ignoredLink(instant));
	}

	public String ignoredLink(StatusInstant<Project> instant) {
		return chainStatus().ignoredLink(instant);
	}

	public ChainStatus chainStatus() {
		return new ChainStatus(project, Project.LINK_FINDER);
	}

	@Override
	public boolean canBeApplied(StatusInstant<Project> statusRequest) {
		return true;
	}
}
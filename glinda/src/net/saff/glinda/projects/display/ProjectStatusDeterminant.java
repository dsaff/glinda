/**
 * 
 */
package net.saff.glinda.projects.display;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusDeterminant;

public abstract class ProjectStatusDeterminant extends
		StatusDeterminant<Project> {
	public abstract boolean canBeApplied(StatusInstant<Project> statusRequest);

	public abstract Status<Project> status(StatusInstant<Project> instant);
}
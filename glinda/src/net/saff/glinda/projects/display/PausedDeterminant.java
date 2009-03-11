/**
 * 
 */
package net.saff.glinda.projects.display;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.ConcreteStatus;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusPrefix;




public class PausedDeterminant extends ProjectStatusDeterminant {
	private final boolean paused;

	public PausedDeterminant(boolean paused) {
		this.paused = paused;
	}
	
	@Override
	public boolean canBeApplied(StatusInstant<Project> statusRequest) {
		return paused;
	}
	
	@Override
	public Status<Project> status(StatusInstant<Project> instant) {
		return new ConcreteStatus<Project>(StatusPrefix.ASLEEP, "paused");
	}
}
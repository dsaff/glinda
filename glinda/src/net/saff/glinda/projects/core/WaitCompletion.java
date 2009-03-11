package net.saff.glinda.projects.core;

import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.ConcreteStatus;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusDeterminant;
import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.time.GlindaTimePoint;

public class WaitCompletion<T extends RequirementNode<T>> extends StatusDeterminant<T> {
	private GlindaTimePoint point;

	public WaitCompletion(GlindaTimePoint point) {
		this.point = point;
	}

	public boolean stillWaiting(GlindaTimePoint glindaTimePoint) {
		return (point != null) && point.isAfter(glindaTimePoint);
	}

	public String comment() {
		return "waiting until " + point.longString();
	}

	@Override
	public boolean canBeApplied(StatusInstant<T> statusRequest) {
		return stillWaiting(statusRequest.getNow());
	}

	@Override
	public Status<T> status(StatusInstant<T> instant) {
		return new ConcreteStatus<T>(StatusPrefix.OK, comment());
	}
}

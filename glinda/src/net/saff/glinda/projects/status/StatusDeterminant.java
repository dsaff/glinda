package net.saff.glinda.projects.status;

import java.io.Serializable;

import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;

public abstract class StatusDeterminant<T extends RequirementNode<T>> implements Serializable {
	public abstract boolean canBeApplied(StatusInstant<T> statusRequest);

	public abstract Status<T> status(StatusInstant<T> instant);
}

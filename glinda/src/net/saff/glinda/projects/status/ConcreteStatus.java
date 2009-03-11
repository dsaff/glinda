package net.saff.glinda.projects.status;

import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;

public class ConcreteStatus<T extends RequirementNode<T>> extends Status<T> {

	private final String[] comments;

	public ConcreteStatus(StatusPrefix prefix, String... comments) {
		super(prefix);
		this.comments = comments;
	}

	public String[] getComments(StatusInstant<T> instant) {
		return comments;
	}

}

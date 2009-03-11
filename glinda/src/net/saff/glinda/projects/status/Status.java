package net.saff.glinda.projects.status;

import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;


public abstract class Status<T extends RequirementNode<T>> {
	private final StatusPrefix prefix;
	
	public abstract String[] getComments(StatusInstant<T> instant);

	public Status(StatusPrefix prefix) {
		this.prefix = prefix;
	}

	public boolean hasPrefix(StatusPrefix prefix) {
		return this.getPrefix().equals(prefix);
	}

	public StatusPrefix getPrefix() {
		return prefix;
	}


	public Status<T> replacePrefixWith(StatusPrefix prefix) {
		final Status<T> parentStatus = this;
		return new Status<T>(prefix) {
			@Override
			public String[] getComments(StatusInstant<T> instant) {
				return parentStatus.getComments(instant);
			}
		};
	}
}

package net.saff.glinda.projects.tasks;

import net.saff.glinda.projects.core.OutgoingLinkFinder;
import net.saff.glinda.projects.core.Project;

public abstract class IgnoredLinks {

	public IgnoredLinks() {
		super();
	}

	protected abstract String descendants(OutgoingLinkFinder<Project> finder);

	public String bracketedDescendants(OutgoingLinkFinder<Project> finder) {
		return " [" + descendants(finder) + "]";
	}

}
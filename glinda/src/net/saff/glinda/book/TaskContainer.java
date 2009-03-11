/**
 * 
 */
package net.saff.glinda.book;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.ProjectContainer;
import net.saff.glinda.projects.tasks.Task;

final class TaskContainer extends ProjectContainer<Task> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Task createNew(BracketedString projectName) {
		return new Task(projectName, this);
	}
}
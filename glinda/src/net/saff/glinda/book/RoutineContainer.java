/**
 * 
 */
package net.saff.glinda.book;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.ProjectContainer;
import net.saff.glinda.projects.routines.Routine;

final class RoutineContainer extends ProjectContainer<Routine> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Routine createNew(BracketedString projectName) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void noteDone(BracketedString name) {
		// do nothing
	}
}
package net.saff.glinda.projects.display;

import net.saff.glinda.projects.core.Project;

// TODO: check package size
public interface ActionableCache {

	Boolean put(Project r, boolean computeActionable);

	boolean containsKey(Project r);

	Boolean get(Project r);

	void remove(Project p);

}

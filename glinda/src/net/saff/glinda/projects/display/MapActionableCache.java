package net.saff.glinda.projects.display;

import java.util.HashMap;
import java.util.Map;

import net.saff.glinda.projects.core.Project;

// TODO: rename methods
public class MapActionableCache implements ActionableCache {
	private Map<Project, Boolean> cache = new HashMap<Project, Boolean>();
	
	public boolean containsKey(Project r) {
		return cache.containsKey(r);
	}

	public Boolean get(Project r) {
		return cache.get(r);
	}

	public Boolean put(Project r, boolean actionable) {
		return cache.put(r, actionable);
	}

	public void remove(Project p) {
		cache.remove(p);
	}

}

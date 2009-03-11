package net.saff.glinda.projects.core;

import java.util.LinkedHashMap;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.names.RenameListener;

public abstract class ProjectContainer<T extends Project> extends
		LinkedHashMap<BracketedString, T> implements RenameListener {
	private static final long serialVersionUID = 1L;

	public T create(BracketedString projectName) {
		T created = createNew(projectName);
		put(projectName, created);
		return created;
	}

	protected abstract T createNew(BracketedString projectName);

	public void noteDone(BracketedString name) {
		remove(name);
	}

	public void hasBeenRenamed(BracketedString oldName, BracketedString newName) {
		if (containsKey(oldName)) {
			put(newName, get(oldName));
			remove(oldName);
		}
	}
}

/**
 * 
 */
package net.saff.glinda.names;

import java.io.Serializable;


public class PrefixedName implements Serializable {
	private final String namePrefix;
	private BracketedString name;

	public PrefixedName(String namePrefix, BracketedString name) {
		this.namePrefix = namePrefix;
		this.name = name;
	}

	public void renameTo(BracketedString newName) {
		this.name = newName;
	}

	@Override
	public String toString() {
		return namePrefix + getName();
	}

	public BracketedString getName() {
		return name;
	}

	public void renameTo(BracketedString newName, RenameListener listener) {
		BracketedString oldName = getName();
		renameTo(newName);
		listener.hasBeenRenamed(oldName, newName);
	}

	public PrefixedName withPrefix(String prefix) {
		return new PrefixedName(prefix, name);
	}

	public String deCamelCase() {
		return name.deCamelCase();
	}
}
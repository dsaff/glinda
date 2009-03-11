/**
 * 
 */
package net.saff.glinda.projects.status;


public enum StatusPrefix {
	NOT_OK("!"), ASLEEP("z"), OK(">");

	private final String prefix;

	StatusPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override public String toString() {
		return prefix;
	}
}
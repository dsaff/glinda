/**
 * 
 */
package net.saff.glinda.names;

import java.io.Serializable;
import java.util.regex.Pattern;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.time.GlindaTimePoint;

public class BracketedString implements Serializable {
	private static final Pattern ENDING_COLON = Pattern.compile(":$");
	private String name;

	public BracketedString(String name) {
		if (name.contains(" ") && !name.startsWith("["))
			this.name = "[" + name + "]";
		else
			this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BracketedString))
			return false;
		BracketedString string = (BracketedString) obj;
		return string.name.equals(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public BracketedString canonicalize() {
		return new BracketedString(ENDING_COLON.matcher(name).replaceFirst(""));
	}

	public void markDoneIn(GlindaTimePoint latestNow,
			RequirementGraph<Project> graph, DoneListener... listeners) {
		// TODO: pass-through
		for (DoneListener each : listeners)
			each.done(this, latestNow, graph);
	}

	public String deCamelCase() {
		StringBuilder builder = new StringBuilder();
		char[] chars = name.toCharArray();
		for (char c : chars) {
			if (Character.isUpperCase(c))
				builder.append(" ").append(Character.toLowerCase(c));
			else
				builder.append(c);
		}

		String trim = builder.toString().trim();
		return initCaps(trim);
	}

	private String initCaps(String trim) {
		return firstChar(trim).toUpperCase() + trim.substring(1).toLowerCase();
	}

	private String firstChar(String trim) {
		return trim.substring(0, 1);
	}
}
package net.saff.glinda.projects.requirement;

import java.util.Collection;
import java.util.EnumSet;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.time.GlindaTimePoint;

public interface RequirementNode<T extends RequirementNode<T>> {
	Collection<T> getBlocked(RequirementGraph<Project> graph);

	boolean doneBetween(GlindaTimePoint beginning, GlindaTimePoint end);

	String nameForStatusLine(EnumSet<Flag> flags);

	String leftArrowString(String lastName, RequirementGraph<T> graph);

	boolean shouldShowBranchedParents();

	boolean intrinsicallyInactionable(StatusInstant<T> now);
	
	void done(GlindaTimePoint now, RequirementGraph<Project> graph);
}

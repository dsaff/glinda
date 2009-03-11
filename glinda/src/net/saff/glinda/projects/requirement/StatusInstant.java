package net.saff.glinda.projects.requirement;

import java.util.Map;

import net.saff.glinda.projects.display.ActionableCache;

public interface StatusInstant<T extends RequirementNode<T>> extends DisplayInstant {
	ActionableCache getActionableCache();

	Map<Object, Cycle<Requirement<T>>> getCycleCache();

	boolean isPretenseReason(T project);

	RequirementGraph<T> getGraph();
}
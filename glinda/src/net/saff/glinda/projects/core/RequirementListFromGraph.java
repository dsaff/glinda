// Copyright 2008 Google Inc. All Rights Reserved.

package net.saff.glinda.projects.core;

import java.util.Collection;

import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.RequirementList;
import net.saff.glinda.projects.requirement.RequirementNode;

public class RequirementListFromGraph<T extends RequirementNode<T>> extends
		RequirementList<T> {
	// TODO: remove RequirementList entirely
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final RequirementGraph<T> graph;
	
	private final T parent;

	public RequirementListFromGraph(RequirementGraph<T> graph, T parent) {
		this.graph = graph;
		this.parent = parent;
	}

	@Override
	public void add(Requirement<T> r) {
		graph.add(parent, r);
	}

	@Override
	public void clear() {
		graph.clear(parent);
	}

	@Override
	public Collection<Requirement<T>> getContents() {
		// TODO Auto-generated method stub
		return graph.getChildren(parent);
	}

	@Override
	public String leftArrowString(String nextName) {
		return graph.leftArrowString(parent, nextName);
	}

	@Override
	public void remove(Requirement<T> requirement) {
		graph.remove(parent, requirement);
	}

	@Override
	public String toString() {
		return graph.toString(parent);
	}
}
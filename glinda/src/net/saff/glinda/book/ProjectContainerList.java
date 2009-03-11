package net.saff.glinda.book;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.core.ProjectContainer;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.tasks.Task;
import net.saff.glinda.time.GlindaTimePoint;

public class ProjectContainerList extends ArrayList<ProjectContainer<?>> {
	private ProjectContainer<?> defaultContainer;

	public static ProjectContainerList fresh() {
		return new ProjectContainerList();
	}
	
	public ProjectContainerList() {
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	private static final Project NULL_PROJECT = Task.withoutListener("nothing");

	public ProjectContainerList with(ProjectContainer<?> container) {
		add(container);
		return this;
	}

	public ProjectContainerList withDefault(ProjectContainer<?> container) {
		defaultContainer = container;
		return with(container);
	}

	@Override
	public boolean add(ProjectContainer<?> o) {
	  // TODO: test in
	  if (o == null)
	    throw new NullPointerException();
	  return super.add(o);
	}
	
	public Project findConcern(BracketedString name) {
		for (Map<BracketedString, ? extends Project> map : this)
			if (map.containsKey(name))
				return map.get(name);

		return ProjectContainerList.NULL_PROJECT;
	}

	public boolean noSuchConcern(BracketedString nextStep) {
		return findConcern(nextStep) == ProjectContainerList.NULL_PROJECT;
	}

	Project startIfNeeded(BracketedString projectName) {
		if (noSuchConcern(projectName))
			defaultContainer.create(projectName);
		return findConcern(projectName);
	}

	public void done(BracketedString name, GlindaTimePoint now, RequirementGraph<Project> graph) {
		// TODO: pass-through
		findConcern(name).done(now, graph);
		for (ProjectContainer<?> each : this)
			each.noteDone(name);
	}

	Map<BracketedString, Project> concernMap() {
		LinkedHashMap<BracketedString, Project> concerns = new LinkedHashMap<BracketedString, Project>();
		for (Map<BracketedString, ? extends Project> container : this)
			concerns.putAll(container);
		return concerns;
	}
}

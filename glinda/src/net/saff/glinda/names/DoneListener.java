package net.saff.glinda.names;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.time.GlindaTimePoint;

public interface DoneListener {

	void done(BracketedString name, GlindaTimePoint latestNow, RequirementGraph<Project> graph);

}

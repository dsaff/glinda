package net.saff.glinda.projects.tasks;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.names.PrefixedName;
import net.saff.glinda.names.RenameListener;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.requirement.SupersmashStrategy;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.time.GlindaTimePoint;

public class Task extends Project {
	boolean foreverDone = false;

	public static Task withoutListener(String name) {
		return withoutListener(new BracketedString(name));
	}

	public static Task withoutListener(BracketedString name) {
		return new Task(name, null);
	}

	public Task(BracketedString name, RenameListener renameListener) {
		this(new PrefixedName("p:", name), renameListener, new IterateSupersmashStrategy());
	}

	public Task(PrefixedName prefixedName, RenameListener renameListener, SupersmashStrategy strategy) {
		// TODO: pass-through
		super(prefixedName, renameListener, strategy);
	}

	@Override
	public Status<Project> activeStatus(GlindaTimePoint now,
			String ignoredLink) {
		return new ChainStatus(this, Project.LINK_FINDER);
	}

	@Override
	public boolean foreverDone() {
		return foreverDone;
	}

	@Override
	public void done(GlindaTimePoint now, RequirementGraph<Project> graph) {
		super.done(now, graph);
		foreverDone = true;
	}

	public boolean doneBetween(GlindaTimePoint beginning, GlindaTimePoint end) {
		return foreverDone();
	}

	@Override
	public Status<Project> blockedNotByRequirementStatus(
			StatusInstant<Project> request) {
		return null;
	}
}

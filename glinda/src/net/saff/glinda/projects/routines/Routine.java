package net.saff.glinda.projects.routines;

import net.saff.glinda.names.PrefixedName;
import net.saff.glinda.names.RenameListener;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.ConcreteStatus;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.time.GlindaTimePoint;

public class Routine extends Project {
	private final RoutineSpecification spec;
	private GlindaTimePoint latestDone = null;

	public static Routine withoutListener(RoutineSpecification spec) {
		return new Routine(spec, null);
	}

	public Routine(RoutineSpecification spec, RenameListener renameListener) {
		super(new PrefixedName("r:", spec.getRoutineName()), renameListener,
				new IterateSupersmashStrategy());
		this.spec = spec;
	}

	public Status<Project> activeStatus(GlindaTimePoint now, String ignoredLink) {
		if (hasInstancePriorTo(now)) {
			GlindaTimePoint mostRecent = spec.lastInstanceBefore(now);
			if (needsToBeDone(mostRecent))
				return new ConcreteStatus<Project>(StatusPrefix.NOT_OK,
						ignoredLink, "since " + mostRecent.toString());
		}
		return new ConcreteStatus<Project>(StatusPrefix.OK, "until "
				+ spec.firstInstanceAfter(now));
	}

	private boolean hasInstancePriorTo(GlindaTimePoint now) {
		return now.isAfter(spec.lastInstanceBefore(now));
	}

	public boolean needsToBeDone(GlindaTimePoint now) {
		return latestDone == null || now.isAfter(latestDone);
	}

	public boolean doneBetween(GlindaTimePoint before, GlindaTimePoint after) {
		if (latestDone == null)
			return false;
		return before.until(after).includes(latestDone.getPoint());
	}

	@Override
	public boolean foreverDone() {
		return false;
	}

	@Override
	public void done(GlindaTimePoint now, RequirementGraph<Project> graph) {
		if (needsToBeDone(now))
			latestDone = now;
		super.done(now, graph);
	}

	@Override
	public Status<Project> blockedNotByRequirementStatus(
			StatusInstant<Project> request) {
		return null;
	}
}

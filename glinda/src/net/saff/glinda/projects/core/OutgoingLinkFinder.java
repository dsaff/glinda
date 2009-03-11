package net.saff.glinda.projects.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.saff.glinda.projects.requirement.Cycle;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.time.GlindaTimePoint;

public abstract class OutgoingLinkFinder<T extends RequirementNode<T>> {
	public abstract Collection<Requirement<T>> getRequirements(T node, RequirementGraph<T> graph);

	public OutgoingLinkFinder<T> finder(final Cycle<Requirement<T>> cycle, final GlindaTimePoint now) {
		return possiblyReplaceWithCycle(cycle, now);
	}

	public OutgoingLinkFinder<T> possiblyReplaceWithCycle(final Cycle<Requirement<T>> cycle, final GlindaTimePoint now) {
		if (cycle == null)
			return this;
		return new OutgoingLinkFinder<T>() {
			public Collection<Requirement<T>> getRequirements(T prerequisite, RequirementGraph<T> graph) {
				for (int i = 0; i < cycle.size(); i++)
					if (cycle.get(i).getPrerequisite(now).equals(prerequisite))
						return listOf(cycle.get((i + 1) % cycle.size()));
				return null;
			}

			private Collection<Requirement<T>> listOf(
					Requirement<T> requirement) {
				List<Requirement<T>> list = new ArrayList<Requirement<T>>();
				list.add(requirement);
				return list;
			}
		};
	}
}

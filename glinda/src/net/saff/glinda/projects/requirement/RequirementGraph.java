package net.saff.glinda.projects.requirement;

import static com.domainlanguage.time.Duration.days;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

public class RequirementGraph<T extends RequirementNode<T>> implements
		Serializable {
	private static final long serialVersionUID = 1L;
	// TODO (Jun 16, 2008 6:27:11 PM): type dup
	private Map<T, Map<T, Requirement<T>>> children = new HashMap<T, Map<T, Requirement<T>>>();
	private Map<T, Map<T, Requirement<T>>> parents = new HashMap<T, Map<T, Requirement<T>>>();
	private Duration defaultLifespan = days(1);

	public RequirementGraph() {
		// TODO Auto-generated constructor stub
	}

	// TODO (May 21, 2008 1:32:31 PM): look for cycles

	public void add(T parent, Requirement<T> r) {
		getChildMap(parent).put(r.getPrerequisite(), r);
		getParentMap(r.getPrerequisite()).put(parent, r);
	}

	public void addReplacing(T parent, Requirement<T> r) {
		// TODO: pass-through
		removeEquivalents(parent, r);
		add(parent, r);
	}

	public void removeEquivalents(T parent, Requirement<T> r) {
		// TODO (Jun 16, 2008 6:31:00 PM): kill strategy
		getChildMap(parent).remove(r.getPrerequisite());
		getParentMap(r.getPrerequisite()).remove(parent);
	}

	public Collection<Requirement<T>> getParents(Requirement<T> r) {
		return getParents(r.getPrerequisite());
	}

	// TODO: DUP below?
	private Collection<Requirement<T>> getParents(T child) {
		return getParentMap(child).values();
	}

	private Map<T, Requirement<T>> getParentMap(T child) {
		if (!parents.containsKey(child))
			parents.put(child, new LinkedHashMap<T, Requirement<T>>());
		Map<T, Requirement<T>> map = parents.get(child);
		return map;
	}

	public Collection<Requirement<T>> getChildren(T parent) {
		return getChildMap(parent).values();
	}

	private Map<T, Requirement<T>> getChildMap(T parent) {
		if (!children.containsKey(parent))
			children.put(parent, new LinkedHashMap<T, Requirement<T>>());
		Map<T, Requirement<T>> map = children.get(parent);
		return map;
	}

	public void clear(T parent) {
		Collection<Requirement<T>> theseChildren = getChildren(parent);
		for (Iterator<Requirement<T>> i = theseChildren.iterator(); i.hasNext();) {
			Requirement<T> requirement = i.next();
			i.remove();
			getParents(requirement).remove(requirement);
		}
	}

	public void remove(T parent, Requirement<T> requirement) {
		getChildren(parent).remove(requirement);
		getParents(requirement).remove(requirement);
	}

	public Requirement<T> firstRequirement(T parent, GlindaTimePoint now) {
		for (Requirement<T> each : getChildren(parent)) {
			if (!each.isDone(now))
				return each;
		}
		return new Requirement<T>(parent, RequirementType.HARD, null,
				TimeInterval.everFrom(TimePoint.from(new Date())));
	}

	public String leftArrowString(T parent, String nextName) {
		// TODO: could this be better? Seems that two-way search is good
		for (Requirement<? extends T> requirement : getChildren(parent))
			if (requirement != null) {
				T prereq = requirement
						.getPrerequisiteRegardlessOfApplicability();
				if (prereq != null && prereq.toString().equals(nextName))
					return requirement.leftArrow();
			}
		return null;
	}

	public String toString(T parent) {
		return getChildren(parent).toString();
	}

	public Collection<T> getParentNodes(T project) {
		Collection<Requirement<T>> rs = getParents(project);
		List<T> ts = new ArrayList<T>();
		for (Requirement<T> r : rs) {
			ts.add(r.getRequirer());
		}
		return ts;
	}

	public Duration getDefaultLifespan() {
		return defaultLifespan;
	}

	public void setDefaultLifespan(Duration expirationTime) {
		defaultLifespan = expirationTime;
	}

	public Duration overrideLifespan(Duration lifespan) {
		return lifespan == null ? getDefaultLifespan() : lifespan;
	}

	public TimeInterval linkDuration(GlindaTimePoint now, Duration lifespan) {
		return now.until(now.plus(overrideLifespan(lifespan)));
	}

	public void priorTo(T project, T nextStep, GlindaTimePoint now,
			Duration untilExpiration) {
		// TODO (Jun 16, 2008 6:35:52 PM): make strategy die
		// TODO (May 21, 2008 1:30:26 PM): too many params

		// TODO (May 21, 2008 1:28:01 PM): DUP?

		// TODO: pass-through
		addReplacing(project, new Requirement<T>(project, RequirementType.SOFT,
				nextStep, linkDuration(now, untilExpiration)));
	}

	public Requirement<T> firstChild(T parent) {
		return getChildren(parent).iterator().next();
	}
}

/**
 * 
 */
package net.saff.glinda.projects.requirement;

import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

import net.saff.glinda.time.GlindaTimePoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public abstract class RequirementList<T extends RequirementNode<T>> implements
		Serializable {
	private final List<Requirement<T>> contents;

	private static final long serialVersionUID = 1L;

	public RequirementList(RequirementList<T> requirements) {
		contents = new ArrayList<Requirement<T>>(requirements.getContents());
	}

	public Collection<? extends Requirement<T>> getContents() {
		return contents;
	}

	public RequirementList() {
		contents = new ArrayList<Requirement<T>>();
	}

	public Requirement<T> firstRequirement(GlindaTimePoint now) {
		for (Requirement<T> each : contents) {
			if (!each.isDone(now))
				return each;
		}
		// TODO: unused, right?
		return new Requirement<T>(null, RequirementType.HARD, null,
				TimeInterval.everFrom(TimePoint.from(new Date())));
	}

	public String leftArrowString(String nextName) {
		for (Requirement<? extends T> requirement : contents)
			if (requirement != null) {
				T prereq = requirement
						.getPrerequisiteRegardlessOfApplicability();
				if (prereq != null && prereq.toString().equals(nextName))
					return requirement.leftArrow();
			}
		return null;
	}

	public void add(Requirement<T> r) {
		contents.add(r);
	}

	public void clear() {
		contents.clear();
	}

	public void remove(Requirement<T> requirement) {
		contents.remove(requirement);
	}

	@Override
	public String toString() {
		return contents.toString();
	}
}

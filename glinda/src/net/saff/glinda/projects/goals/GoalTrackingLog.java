/**
 * 
 */
package net.saff.glinda.projects.goals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.ProjectContainer;
import net.saff.glinda.projects.requirement.SupersmashStrategy;

public final class GoalTrackingLog extends ProjectContainer<Goal> implements
		GoalListener, Serializable {
	private static final long serialVersionUID = 1L;
	private final SupersmashStrategy strategy;

	public GoalTrackingLog(SupersmashStrategy strategy) {
		this.strategy = strategy;
		// TODO Auto-generated constructor stub
	}
	
	public Goal getOrCreate(BracketedString goalName) {
		if (!containsKey(goalName))
			put(goalName, createNew(goalName));
		return super.get(goalName);
	}

	private void sendToEnd(BracketedString goalName) {
		Goal status = get(goalName);
		remove(goalName);
		put(goalName, status);
	}

	public void paused(BracketedString name) {
		sendToEnd(name);
	}

	public ArrayList<BracketedString> goalNames() {
		return new ArrayList<BracketedString>(keySet());
	}

	public Collection<Goal> goals() {
		return values();
	}

	@Override
	protected Goal createNew(BracketedString projectName) {
		return new Goal(projectName, this, strategy);
	}

	public Goal getGoal(BracketedString bracketedString) {
		BracketedString name = bracketedString.canonicalize();
		Goal goal = get(name);
		if (goal == null)
			throw new IllegalArgumentException(name + " is not a goal");
		return goal;
	}
}
package net.saff.glinda.projects.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.names.PrefixedName;
import net.saff.glinda.names.RenameListener;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.RequirementList;
import net.saff.glinda.projects.requirement.RequirementNode;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.requirement.SupersmashStrategy;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.time.GlindaTimePoint;

public abstract class Project implements RequirementNode<Project>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static OutgoingLinkFinder<Project> LINK_FINDER = new OutgoingLinkFinder<Project>() {
		public Collection<Requirement<Project>> getRequirements(
				Project prerequisite, RequirementGraph<Project> graph) {
			return graph.getChildren(prerequisite);
		}
	};

	private boolean paused = false;
	private WaitCompletion<Project> waitCompletion = new WaitCompletion<Project>(
			null);
	private boolean trip = false;
	private final PrefixedName prefixedName;
	private final RenameListener renameListener;
	private int cachedHashCode = -1;

	private SupersmashStrategy strategy;

	public Project(PrefixedName prefixedName, RenameListener renameListener,
			SupersmashStrategy strategy) {
		this.prefixedName = prefixedName;
		this.renameListener = renameListener;
		this.setStrategy(strategy);
	}

	@Override
	public String toString() {
		return getNameWithoutPrefix().toString();
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public abstract boolean foreverDone();

	public void renameTo(BracketedString newName) {
		RenameListener renameListener2 = renameListener;
		prefixedName.renameTo(newName, renameListener2);
	}

	public BracketedString getNameWithoutPrefix() {
		return getPrefixedName(Flag.NONE).getName();
	}

	public boolean isPaused() {
		return paused;
	}

	public void setWaitTime(GlindaTimePoint waitTime) {
		this.setWaitCompletion(new WaitCompletion<Project>(waitTime));
	}

	public void addLink(Requirement<Project> link,
			RequirementGraph<Project> graph) {
		// TODO: pass-through
		graph.addReplacing(this, link);
	}

	public void addRequirement(Requirement<Project> requirement,
			RequirementGraph<Project> graph) {
		getRequirements(graph).add(requirement);
	}

	public void done(GlindaTimePoint now, RequirementGraph<Project> graph) {
		getRequirements(graph).clear();
	}

	public void trip() {
		trip = true;
	}

	public boolean shouldShowBranchedParents() {
		return trip;
	}

	public final String nameForStatusLine(EnumSet<Flag> flags) {
		return getPrefixedName(flags).toString();
	}

	public void setWaitCompletion(WaitCompletion<Project> waitCompletion) {
		this.waitCompletion = waitCompletion;
	}

	public WaitCompletion<Project> getWaitCompletion() {
		return waitCompletion;
	}

	boolean stillWaiting(GlindaTimePoint now) {
		return getWaitCompletion().stillWaiting(now);
	}

	public boolean intrinsicallyInactionable(
			StatusInstant<Project> statusRequest) {
		return isPaused() || stillWaiting(statusRequest.getNow())
				|| foreverDone() || statusRequest.isPretenseReason(this);
	}

	public Collection<Project> getBlocked(RequirementGraph<Project> graph) {
		return graph.getParentNodes(this);
	}

	public PrefixedName getPrefixedName(EnumSet<Flag> flags) {
		if (flags.contains(Flag.distinguishTrips) && trip)
			return prefixedName.withPrefix("t:");
		return prefixedName;
	}

	void harden(boolean cycleExists, GlindaTimePoint now,
			RequirementGraph<Project> graph) {
		Requirement<Project> requirement = graph.firstRequirement(this, now);
		getRequirements(graph).remove(requirement);

		if (cycleExists)
			addLink(requirement.copy(now), graph);
		else
			addLink(requirement.harden(now), graph);
	}

	public abstract Status<Project> blockedNotByRequirementStatus(
			StatusInstant<Project> request);

	public RequirementList<Project> getRequirements(
			final RequirementGraph<Project> graph) {
		return new RequirementListFromGraph<Project>(graph, this);
	}

	public String leftArrowString(String nextName,
			RequirementGraph<Project> graph) {
		return getRequirements(graph).leftArrowString(nextName);
	}

	public Status<Project> activeStatus(StatusInstant<Project> instant,
			String ignoredLink) {
		return activeStatus(instant.getNow(), ignoredLink);
	}

	protected abstract Status<Project> activeStatus(GlindaTimePoint now,
			String ignoredLink);

	// TODO (Jun 23, 2008 1:18:07 PM): clearRemovesDoneItems should be used
	public void clear(GlindaTimePoint now,
			@SuppressWarnings("unused") boolean clearRemovesDoneItems,
			RequirementGraph<Project> graph) {
		Requirement<Project> requirement = graph.firstRequirement(this, now);
		getRequirements(graph).remove(requirement);
	}

	public String heapifyChoice(int optionNumber) {
		return optionNumber + ") " + prefixedName.deCamelCase() + ".";
	}

	@Override
	public int hashCode() {
		if (cachedHashCode == -1)
			cachedHashCode = toString().hashCode();
		return cachedHashCode;
	}

	public void setStrategy(SupersmashStrategy strategy) {
		this.strategy = strategy;
	}

	public SupersmashStrategy getStrategy() {
		return strategy;
	}
}

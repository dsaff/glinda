package net.saff.glinda.projects.display;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Cycle;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.time.GlindaTimePoint;

public class StatusRequest implements StatusInstant<Project> {
	private static final EnumSet<Flag> NO_FLAGS = EnumSet.noneOf(Flag.class);

	public class Builder {
		public Builder pretendingFor(Project reason) {
			pretenseReason = reason;
			return this;
		}

		public Builder withFlags(EnumSet<Flag> newFlags) {
			// TODO: remove null possibility
			if (newFlags != null)
				flags = newFlags;
			return this;
		}

		public StatusRequest makeRequest() {
			return StatusRequest.this;
		}
	}

	public static StatusRequest createStatusRequest(GlindaTimePoint now,
			Project object, EnumSet<Flag> of) {
		return asOf(now, new RequirementGraph<Project>()).pretendingFor(object)
				.withFlags(of).makeRequest();
	}

	// TODO: make names better
	public static Builder asOf(GlindaTimePoint now,
			RequirementGraph<Project> graph) {
		// TODO: pass through
		return new StatusRequest(now, graph).new Builder();
	}

	private final GlindaTimePoint now;
	private EnumSet<Flag> flags = NO_FLAGS;
	private final ActionableCache actionableCache = new MapActionableCache();
	private final Map<Object, Cycle<Requirement<Project>>> cycleCache = new HashMap<Object, Cycle<Requirement<Project>>>();
	private Project pretenseReason = null;
	private final RequirementGraph<Project> graph;

	private StatusRequest(GlindaTimePoint now, RequirementGraph<Project> graph) {
		this.graph = graph;
		// TODO: make todo tags
		// TODO: handle null in builder
		this.now = now != null ? now : GlindaTimePoint.now();
		// TODO Auto-generated constructor stub
	}

	public GlindaTimePoint getNow() {
		return now;
	}

	public boolean shouldShowActionItemsFirst() {
		return flags.contains(Flag.actionItemsFirst);
	}

	public boolean shouldShowParents() {
		return flags.contains(Flag.withParents);
	}

	public EnumSet<Flag> getFlags() {
		return flags;
	}

	public String display(GlindaTimePoint time) {
		if (time == null)
			return null;
		if (getFlags().contains(Flag.showFullTimes))
			return time.toString();
		else
			return time.displayRelativeTo(getNow());
	}

	public ActionableCache getActionableCache() {
		return actionableCache;
	}

	public Map<Object, Cycle<Requirement<Project>>> getCycleCache() {
		return cycleCache;
	}

	public String format(String statusName, Status<Project> status) {
		StringBuilder builder = new StringBuilder();
		builder.append(status.getPrefix());
		builder.append(" ");
		builder.append(statusName);
		builder.append(parensIfNeeded(join(", ", status.getComments(this))));
		return builder.toString();
	}

	String parensIfNeeded(String string) {
		if (string.length() == 0)
			return "";
		if (alreadyStartsWithPunctuation(string)) {
			if (string.contains(", "))
				return string.replaceFirst(", ", " (") + ")";
			return string;
		}
		return String.format(" (%s)", string);
	}

	private boolean alreadyStartsWithPunctuation(String string) {
		return string.startsWith(" <") || string.startsWith(" [")
				|| string.startsWith("\n");
	}

	public String join(String connector, String... items) {
		if (items.length == 0)
			return "";
		String returnThis = "";
		for (String item : items)
			if (item != null && item.length() > 0)
				returnThis += item + connector;
		if (returnThis.length() == 0)
			return "";
		int finalLength = returnThis.length() - connector.length();
		return returnThis.substring(0, finalLength);
	}

	public boolean isPretenseReason(Project project) {
		return project == pretenseReason;
	}

	public RequirementGraph<Project> getGraph() {
		return graph;
	}

	public static Builder asOf(GlindaTimePoint now, LoqBook book) {
		return asOf(now, book.getGraph());
	}
}

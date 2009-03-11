/**
 * 
 */
package net.saff.glinda.projects.goals;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.months;
import static java.util.Arrays.asList;

import java.util.EnumSet;
import java.util.List;

import net.saff.glinda.interpretation.declaring.PropertyAssignment;
import net.saff.glinda.interpretation.invoking.Delegate;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.names.PrefixedName;
import net.saff.glinda.names.RenameListener;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.requirement.SupersmashStrategy;
import net.saff.glinda.projects.status.ConcreteStatus;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.projects.tasks.ChainStatus;
import net.saff.glinda.projects.tasks.Task;
import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;

public class Goal extends Task {
	private final TrackHistory tracks = new TrackHistory();

	public Goal(BracketedString name, RenameListener renameListener,
			SupersmashStrategy strategy) {
		super(new PrefixedName("", name), renameListener, strategy);
	}

	@Override
	public Status<Project> blockedNotByRequirementStatus(
			StatusInstant<Project> request) {
		return activeStatus(request, null).replacePrefixWith(StatusPrefix.OK);
	}

	public Status<Project> activeStatus(GlindaTimePoint now, String ignoredLink) {
		Status<Project> mostConcerning = null;
		for (Duration each : comparisonDurations()) {
			Status<Project> newStatus = status(ignoredLink, each, now);
			if (newStatus.hasPrefix(StatusPrefix.NOT_OK))
				return newStatus;
			if (mostConcerning == null)
				mostConcerning = newStatus;
		}
		return mostConcerning;
	}

	private Status<Project> status(String ignoredLink, Duration duration,
			GlindaTimePoint now) {
		final AbsoluteValueDifference difference = tracks
				.differenceSinceYesterday(now, duration);
		return status(difference, ignoredLink);
	}

	public List<Duration> comparisonDurations() {
		return asList(getDefaultComparisonTimeframe(), days(7), months(1));
	}

	public Status<Project> status(final AbsoluteValueDifference difference,
			final String ignoredLink) {
		if (difference.fallingBehindVelocity())
			return shortfallStatus(difference, null, ignoredLink);

		if (tracks.velocityNeedsTrack(difference))
			return new ConcreteStatus<Project>(StatusPrefix.NOT_OK, tracks
					.timeSinceLastTrack(difference.getNow())
					+ " since last track");

		return new Status<Project>(difference.prefixChar()) {
			@Override
			public String[] getComments(StatusInstant<Project> request) {
				EnumSet<Flag> flags = request.getFlags();
				return difference.comments(flags.contains(Flag.withTimes),
						ignoredLink, new ChainStatus(Goal.this,
								Project.LINK_FINDER)
								.possiblyMultipleParents(request));
			}
		};
	}

	public Status<Project> shortfallStatus(AbsoluteValueDifference difference,
			String blockedComment, String ignoredLink) {
		GlindaTimePoint now = difference.getNow();
		String needs = String.format("needs %.1f %s", shortfall(difference),
				sinceWhen(shortfall(difference), now));
		if (canBePostponed(shortfall(difference))) {
			return new ConcreteStatus<Project>(
					StatusPrefix.OK,
					wasComment(difference),
					needs,
					"needs 1.0 by "
							+ now
									.timeWhenOneIsReachedAtThisRate(shortfall(difference)));
		} else {
			return new ConcreteStatus<Project>(StatusPrefix.NOT_OK,
					ignoredLink, wasComment(difference), needs, blockedComment);
		}
	}

	private String wasComment(AbsoluteValueDifference difference) {
		return difference.wasComment(false);
	}

	private double shortfall(AbsoluteValueDifference difference) {
		return difference.shortfall(tracks.velocityBeginning());
	}

	@Override
	public boolean intrinsicallyInactionable(StatusInstant<Project> instant) {
		return super.intrinsicallyInactionable(instant)
				|| activeStatus(instant.getNow(), null).hasPrefix(
						StatusPrefix.OK);
	}

	private boolean canBePostponed(double shortfall) {
		return shortfall < 1;
	}

	private String sinceWhen(double shortfall, GlindaTimePoint now) {
		if (canBePostponed(shortfall))
			return "now";
		return "since " + tracks.latestEntry().displayRelativeTo(now);
	}

	public Double track(GlindaTimePoint key, double value) {
		return tracks.track(key, value);
	}

	public boolean isFresh() {
		return tracks.isEmpty() && !isPaused();
	}

	public GlindaTimePoint comparedTimepoint(GlindaTimePoint now) {
		return tracks.timeYesterday(now);
	}

	public Duration getDefaultComparisonTimeframe() {
		return tracks.getDefaultComparisonTimeframe();
	}

	public double yesterdaysDouble(GlindaTimePoint now) {
		return tracks.yesterdaysValue(now).asDouble();
	}

	@Delegate
	public GoalSettings getSettings() {
		return tracks.getSettings();
	}

	public List<String> externalize(EnumSet<Flag> flags) {
		return getSettings().settingsCommands(nameForStatusLine(flags));
	}

	public void set(PropertyAssignment assignment)
			throws LoqCommandExecutionException {
		assignment.apply(this);
	}
}
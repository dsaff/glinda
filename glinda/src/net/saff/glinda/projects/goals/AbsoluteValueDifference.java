/**
 * 
 */
package net.saff.glinda.projects.goals;

import java.util.ArrayList;
import java.util.List;

import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.time.GlindaTimePoint;

public class AbsoluteValueDifference {
	private TimedValue yesterdayValue;
	private TimedValue todayValue;
	private GoalSettings settings;
	private final GoalComparisonInterval interval;

	public AbsoluteValueDifference(TimedValue yesterday, TimedValue now,
			GoalSettings settings, GoalComparisonInterval interval) {
		if (yesterday == null)
			throw new NullPointerException();
		this.yesterdayValue = yesterday;
		this.todayValue = now;
		this.interval = interval;
		this.settings = settings;
	}

	private boolean isYoung() {
		return yesterdayValue.isNull() && !todayValue.isNull();
	}

	public String[] comments(boolean shouldShowTimes, String firstComment,
			String lastComment) {
		List<String> comments = new ArrayList<String>();
		comments.add(firstComment);
		comments.add(wasComment(shouldShowTimes));
		comments.add("is " + display(todayValue, shouldShowTimes));
		if (shouldShowTimes)
			comments.add("next track " + nextTrack());
		comments.add(lastComment);
		return comments.toArray(new String[0]);
	}

	public String wasComment(boolean shouldShowTimes) {
		return "was " + display(yesterdayValue, shouldShowTimes)
				+ interval.agoString();
	}

	private Object display(TimedValue value, boolean shouldShowTimes) {
		if (value == null)
			return null;
		return value.display(getNow(), shouldShowTimes);
	}

	public String nextTrack() {
		return interval.whenShouldItHappenNext(todayValue.getTime());
	}

	public double shortFallSinceYesterday() {
		return -todayValue.howMuchMoreThan(yesterdayValue);
	}

	public GlindaTimePoint getNow() {
		return interval.getNow();
	}

	public boolean fallingBehindVelocity() {
		if (yesterdayValue.isNull() || todayValue.isNull())
			return false;
		return settings.isCompareVelocity()
				&& !yesterdayValue.sameOrLessThan(todayValue);
	}

	public TimedValue getYesterday() {
		return yesterdayValue;
	}

	public StatusPrefix prefixChar() {
		if (todayValue.isNull())
			return StatusPrefix.NOT_OK;
		if (isYoung() || todayValue.sameValue(yesterdayValue)
				|| settings.isMoreIsBetter()
				^ todayValue.lessThan(yesterdayValue))
			return StatusPrefix.OK;
		return StatusPrefix.NOT_OK;
	}

	public double shortfall(GlindaTimePoint beginning) {
		return shortFallSinceYesterday() * beginning.daysUntil(getNow());
	}
}
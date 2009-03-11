/**
 * 
 */
package net.saff.glinda.projects.goals;

import static com.domainlanguage.time.Duration.days;
import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;

public class GoalComparisonInterval {
	private GlindaTimePoint nowtime;
	private Duration comparisonTimeFrame;

	public GoalComparisonInterval(GlindaTimePoint nowtime,
			Duration comparisonTimeFrame) {
		this.nowtime = nowtime;
		this.comparisonTimeFrame = comparisonTimeFrame;
	}

	public GoalComparisonInterval(GlindaTimePoint time) {
		this(time, GoalSettings.DEFAULT_TIME_FRAME);
	}

	public GlindaTimePoint getNow() {
		return nowtime;
	}

	public Duration getComparisonTimeFrame() {
		return comparisonTimeFrame;
	}

	public GlindaTimePoint getThen() {
		return nowtime.minus(comparisonTimeFrame);
	}

	public String whenShouldItHappenNext(GlindaTimePoint lastTime) {
		return lastTime.whenShouldItHappenNext(comparisonTimeFrame)
				.displayRelativeTo(nowtime);
	}

	public String agoString() {
		if (isADayAgo())
			return "";
		return " (" + comparisonTimeFrame + " ago)";
	}

	private boolean isADayAgo() {
		if (!comparisonTimeFrame.normalizedUnit().isConvertibleTo(
				days(1).normalizedUnit()))
			return false;
		return comparisonTimeFrame.compareTo(days(1)) <= 0;
	}
}
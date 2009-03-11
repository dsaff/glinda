package net.saff.glinda.projects.goals;

import static com.domainlanguage.time.Duration.hours;
import static java.util.Arrays.asList;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.domainlanguage.time.Duration;

public class GoalSettings implements Serializable {
	public static final Duration DEFAULT_TIME_FRAME = hours(23);

	public static GoalSettings defaults() {
		return new GoalSettings(false, false, DEFAULT_TIME_FRAME);
	}

	private boolean moreIsBetter;
	private boolean compareVelocity;
	private Duration comparisonTimeframe;

	public GoalSettings(boolean moreIsBetter, boolean compareVelocity,
			Duration comparisonTimeframe) {
		this.setMoreIsBetter(moreIsBetter);
		this.setCompareVelocity(compareVelocity);
		this.setComparisonTimeframe(comparisonTimeframe);
	}

	
	public void setMoreIsBetter(boolean moreIsBetter) {
		this.moreIsBetter = moreIsBetter;
	}

	public boolean isMoreIsBetter() {
		return moreIsBetter;
	}

	public void setCompareVelocity(boolean compareVelocity) {
		this.compareVelocity = compareVelocity;
	}

	public boolean isCompareVelocity() {
		return compareVelocity;
	}

	public void setComparisonTimeframe(Duration comparisonTimeframe) {
		this.comparisonTimeframe = comparisonTimeframe;
	}

	public Duration getDefaultComparisonTimeframe() {
		return comparisonTimeframe;
	}

	@Override public boolean equals(Object obj) {
		GoalSettings settings = (GoalSettings) obj;
		return settings.compareVelocity == compareVelocity
				&& settings.moreIsBetter == moreIsBetter
				&& settings.comparisonTimeframe.equals(comparisonTimeframe);
	}

	public List<String> settingsCommands(String goalName) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("#startGoal " + goalName);
		Field[] fields = GoalSettings.class.getDeclaredFields();
		List<Field> fieldList = asList(fields);
		Collections.sort(fieldList, new Comparator<Field>() {
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Field each : fields) {
			if (!Modifier.isStatic(each.getModifiers()))
				try {
					commands.add(String.format("#set %s %s=%s", goalName, each
							.getName(), each.get(this)));
				} catch (Exception e) {
					throw new RuntimeException(
							"Should never happen.  I assume we can get a field on the class of the declaring object.");
				}
		}
		return commands;
	}

	public GoalSettings replaceComparisonTimeframe(Duration duration) {
		return new GoalSettings(isMoreIsBetter(), isCompareVelocity(), duration);
	}
}
package net.saff.glinda.projects.routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeUnit;


public class DurationString {
	public String duration;

	public DurationString(String duration) {
		this.duration = duration;
	}

	public Duration parse() {
		if (duration == null)
			return null;
		Matcher timeMatcher = Pattern.compile("(.*?) ?(h|hours?|m|minutes?|d|days?|w|weeks?)")
				.matcher(duration);
		if (timeMatcher.matches())
			return new Duration(Integer.valueOf(timeMatcher.group(1)),
					unit(timeMatcher.group(2)));
		throw new RuntimeException("unrecogized duration string " + duration);
	}

	private TimeUnit unit(String group) {
		if (group.startsWith("h"))
			return TimeUnit.hour;
		if (group.startsWith("m"))
			return TimeUnit.minute;
		if (group.startsWith("d"))
			return TimeUnit.day;
		if (group.startsWith("w"))
			return TimeUnit.week;
		return null;
	}
}
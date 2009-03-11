package net.saff.glinda.projects.routines;

import static com.domainlanguage.time.Duration.days;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;

import com.domainlanguage.time.Duration;

public class RoutineSpecificationParser {
	public RoutineSpecification parse2(BracketedString name, String string,
			GlindaTimePoint now) {
		if (string.contains("starting")) {
			String[] split = string.split(" starting ");
			Duration duration = new DurationString(split[0]
					.replace("each ", "")).parse();
			String startString = split[1];

			GlindaTimePoint startTime = startTime(now, startString);
			return new CustomDurationRoutineSpecification(name, duration,
					startTime);
		}

		return new RelativeTimeRoutineSpecification(name, string.replace(
				"each ", "").replaceAll("^day at ", ""));
	}

	public GlindaTimePoint startTime(GlindaTimePoint now, String startString) {
		Matcher matcher = Pattern.compile("[A-Z][a-z][a-z] [0-9]+").matcher(
				startString);
		if (matcher.find())
			return new GlindaTimePointParser().moveToDate(now, matcher.group())
					.moveToMeridianString(startString);
		if (startString.contains("yesterday"))
			return now.minus(days(1)).moveToMeridianString(startString);
		return new RelativeTime(startString).after(now);
	}

	public RoutineSpecification parseWithName(String string,
			GlindaTimePoint latestNow) {
		int firstSpace = string.indexOf(' ');
		return new RoutineSpecificationParser().parse2(new BracketedString(
				string.substring(0, firstSpace)), string
				.substring(firstSpace + 1), latestNow);
	}

}

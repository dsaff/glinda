/**
 * 
 */
package net.saff.glinda.projects.routines;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.months;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.saff.glinda.time.DayOfWeek;
import net.saff.glinda.time.GlindaTimePoint;

public enum RelativeTimeUnit {
	hourOfDay {

		public GlindaTimePoint push(GlindaTimePoint now,
				TimeDirection direction, String timeString2) {
			GlindaTimePoint thatTimeToday = now
					.moveToMeridianString(timeString2);
			return direction.pushPast(thatTimeToday, now, days(1));
		}
	},
	dayOfWeek {
		public Set<DayOfWeek> daysOfWeek(String timeString) {
			EnumSet<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
			String[] split = timeString.split(",|(, )?and");
			for (String component : split) {
				Matcher matcher = Pattern.compile("^(next )?([A-Z][a-z]+day)")
						.matcher(component.trim());
				if (matcher.find())
					days.add(DayOfWeek.valueOf(matcher.group(2)));
			}

			return days;
		}

		public GlindaTimePoint push(GlindaTimePoint now,
				TimeDirection direction, String timeString) {
			Set<DayOfWeek> daysOfWeek = daysOfWeek(timeString);
			if (!daysOfWeek.isEmpty())
				for (int i = 0; i < 7; i++) {
					GlindaTimePoint pushedDay = direction.push(now, days(i));
					if (daysOfWeek.contains(DayOfWeek.from(pushedDay)))
						return pushedDay;
				}
			return now;
		}
	},
	dayOfMonth {
		private GlindaTimePoint closestInstance(GlindaTimePoint now,
				int dayOfMonth) {
			if (now == null)
				return null;
			if (dayOfMonth == -1)
				return now;
			return now.moveToDayOfMonth(dayOfMonth);
		}

		public int dayOfMonth(String timeString) {
			Matcher matcher = Pattern.compile("on the ([0-9]+)").matcher(
					timeString);
			if (matcher.find())
				return Integer.valueOf(matcher.group(1));
			return -1;
		}

		public GlindaTimePoint push(GlindaTimePoint now,
				TimeDirection direction, String timeString) {
			GlindaTimePoint thatTimeToday = closestInstance(now,
					dayOfMonth(timeString));
			return direction.pushPast(thatTimeToday, now, months(1));
		}
	};

	public abstract GlindaTimePoint push(GlindaTimePoint now,
			TimeDirection direction, String timeString);

}
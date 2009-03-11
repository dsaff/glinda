/**
 * 
 */
package net.saff.glinda.time;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.weeks;

import java.util.Calendar;

import net.saff.stubbedtheories.ToStringEquality;

public enum DayOfWeek implements ToStringEquality {
	Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;

	public static DayOfWeek fromInt(int dayOfWeek) {
		return values()[mod7(dayOfWeek)];
	}

	private static int mod7(int dayOfWeek) {
		if (dayOfWeek < 1)
			return mod7(dayOfWeek + 7);
		return (dayOfWeek - 1) % 7;
	}

	public int asInt() {
		return ordinal() + 1;
	}

	public int daysAfter(DayOfWeek dayOfWeek) {
		return (asInt() - dayOfWeek.asInt() + 7) % 7;
	}

	public GlindaTimePoint pushBackToDayOfWeek(GlindaTimePoint now) {
		return mostRecent(now);
	}

	public GlindaTimePoint mostRecent(GlindaTimePoint now) {
		int backwardsDays = DayOfWeek.from(now).daysAfter(this);
		GlindaTimePoint thatDayThisWeek = now.minus(days(backwardsDays));
		if (thatDayThisWeek.isAfter(now))
			return thatDayThisWeek.minus(weeks(1));
		return thatDayThisWeek;
	}
	
	public DayOfWeek plus(int i) {
		return fromInt(asInt() + i);
	}

	public static DayOfWeek from(GlindaTimePoint glindaTimePoint) {
		return fromInt(glindaTimePoint.getField(Calendar.DAY_OF_WEEK));
	}
}
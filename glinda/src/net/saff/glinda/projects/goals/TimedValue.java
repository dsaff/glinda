package net.saff.glinda.projects.goals;

import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.Never;

import com.domainlanguage.time.Duration;

public class TimedValue {
	public static final TimedValue NULL = new TimedValue(GoalValue.NULL,
			Never.INSTANCE);

	private GoalValue value;
	GlindaTimePoint when;

	public TimedValue(GoalValue value, GlindaTimePoint when) {
		if (value == null)
			throw new NullPointerException();
		this.value = value;
		this.when = when;
	}

	public double howMuchMoreThan(TimedValue yesterday) {
		return asDouble() - yesterday.asDouble();
	}

	public double asDouble() {
		return value().asDouble();
	}

	public boolean isNull() {
		return value().isNull();
	}

	public boolean sameOrLessThan(TimedValue yesterday) {
		return value().sameOrLessThan(yesterday.value());
	}

	public GoalValue value() {
		return value;
	}

	@Override public String toString() {
		return value.toString();
	}

	public String display(GlindaTimePoint now, boolean shouldShowTimes) {
		if (shouldShowTimes)
			return String.format("%s at %s", this, when.displayRelativeTo(now));
		return toString();
	}
	
	@Override public boolean equals(Object obj) {
		if (!TimedValue.class.isInstance(obj))
			return false;
		TimedValue tvalue = (TimedValue) obj;
		return (tvalue.value.equals(value) && tvalue.when.equals(when));
	}

	public boolean lessThan(TimedValue yesterdayValue) {
		return value.lessThan(yesterdayValue.value);
	}

	public boolean sameValue(TimedValue yesterdayValue) {
		return value.equals(yesterdayValue.value);
	}

	public GlindaTimePoint getTime() {
		return when;
	}

	public String agoSgring(Duration duration, GlindaTimePoint now) {
		return agoString(duration, now);
	}

	public String agoString(Duration duration, GlindaTimePoint now) {
		return this + ": " + duration + " ago at "
				+ getTime().displayRelativeTo(now);
	}
}
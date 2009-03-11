/**
 * 
 */
package net.saff.glinda.time.comparison;

import net.saff.glinda.time.GlindaTimePoint;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import com.domainlanguage.time.Duration;

public class DurationComparison {
	private final Duration duration;
	private final DurationComparisonType type;
	
	public static DurationComparison moreThan(Duration duration) {
		return new DurationComparison(duration, DurationComparisonType.more_than);
	}

	public static DurationComparison atLeast(Duration duration) {
		return new DurationComparison(duration, DurationComparisonType.at_least);
	}

	public static DurationComparison atMost(Duration duration) {
		return new DurationComparison(duration, DurationComparisonType.at_most);
	}

	public static DurationComparison lessThan(Duration duration) {
		return new DurationComparison(duration, DurationComparisonType.less_than);
	}

	DurationComparison(Duration duration, DurationComparisonType type) {
		this.duration = duration;
		this.type = type;
	}

	public Matcher<GlindaTimePoint> before(final GlindaTimePoint referenceTime) {
		return matcher(referenceTime, TimeComparison.before);
	}

	public TypeSafeMatcher<GlindaTimePoint> after(final GlindaTimePoint referenceTime) {
		return matcher(referenceTime, TimeComparison.after);
	}

	public TypeSafeMatcher<GlindaTimePoint> matcher(final GlindaTimePoint t,
			final TimeComparison timeDirection) {
		return new TypeSafeMatcher<GlindaTimePoint>() {
			@Override
			public boolean matchesSafely(GlindaTimePoint dateToCompare) {
				return isWithinInterval(t, timeDirection,
						dateToCompare);
			}

			public void describeTo(Description d) {
				d.appendText(String.format("%s %s %s", DurationComparison.this,
						timeDirection, t.longString()));
			}
		};
	}

	private boolean isWithinInterval(final GlindaTimePoint t,
			final TimeComparison timeDirection, GlindaTimePoint dateToCompare) {
		int adjustedComparison = timeDirection.adjustedComparison(
				t, dateToCompare, duration);
		return type.shouldAllowComparison(adjustedComparison);
	}

	public String toString() {
		return String.format("%s %s", type, duration);
	}
}
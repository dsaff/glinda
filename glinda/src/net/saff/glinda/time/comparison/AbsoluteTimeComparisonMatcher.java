package net.saff.glinda.time.comparison;

import static net.saff.glinda.time.comparison.DurationComparison.moreThan;
import static org.hamcrest.CoreMatchers.describedAs;

import net.saff.glinda.time.GlindaTimePoint;

import org.hamcrest.Matcher;

import com.domainlanguage.time.Duration;

public class AbsoluteTimeComparisonMatcher {
	public static Matcher<GlindaTimePoint> before(GlindaTimePoint otherTime) {
		return matcher(TimeComparison.before, otherTime);
	}

	public static Matcher<GlindaTimePoint> after(GlindaTimePoint t) {
		return matcher(TimeComparison.after, t);
	}
	
	private static Matcher<GlindaTimePoint> matcher(TimeComparison direction, GlindaTimePoint t) {
		String description = direction + " " + t.longString();
		return describedAs(description, moreThan(Duration.NONE).matcher(
				t, direction));
	}
}

package test.net.saff.glinda.units.time;

import static com.domainlanguage.time.Duration.hours;
import static com.domainlanguage.time.Duration.years;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static net.saff.glinda.time.comparison.AbsoluteTimeComparisonMatcher.after;
import static net.saff.glinda.time.comparison.AbsoluteTimeComparisonMatcher.before;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.SystemClock;

@RunWith(Theories.class)
public class TestTimeMatchers {
	@DataPoint
	public static GlindaTimePoint NOW = GlindaTimePoint.now();
	@DataPoint
	public static GlindaTimePoint LATER = NOW.plus(hours(1));
	@DataPoint
	public static TimePoint MUCH_LATER = SystemClock.now().plus(years(1));

	@Theory
	public void afterIsOppositeOfBefore(GlindaTimePoint a, GlindaTimePoint b) {
		assumeThat(a, not(b));
		assertEquals(trueThat(a, before(b)), trueThat(b, after(a)));
	}

	private <T> boolean trueThat(T value, Matcher<T> matcher) {
		return matcher.matches(value);
	}

	@Theory
	public void laterDateIsAfter(GlindaTimePoint a) {
		assertThat(a.plus(hours(1)), after(a));
	}

	@Theory
	public void earlierDateIsNotAfter(GlindaTimePoint a) {
		assertThat(a.minus(hours(1)), not(after(a)));
	}

	@Test
	public void afterToString() {
		assertThat(after(new GlindaTimePointParser().parse("2007-01-01 07:00:00"))
				.toString(), is("after 2007-01-01 07:00:00"));
	}

	@Test
	public void beforeToString() {
		assertThat(before(new GlindaTimePointParser().parse("2007-01-01 07:00:00"))
				.toString(), is("before 2007-01-01 07:00:00"));
	}

	@Theory
	public void afterToStringContainsYear(GlindaTimePoint t) {
		assertThat(after(t).toString(), containsString(t.getYear()));
	}
}

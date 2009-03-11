/**
 * 
 */
package test.net.saff.glinda.units.time;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.hours;
import static com.domainlanguage.time.Duration.minutes;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static net.saff.glinda.time.comparison.DurationComparison.atLeast;
import static net.saff.glinda.time.comparison.DurationComparison.atMost;
import static net.saff.glinda.time.comparison.DurationComparison.lessThan;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class TestDateComparisonMatchers extends LoqBookDataPoints {
	
	@Theory
	public void atLeastMakesSense(GlindaTimePoint now, int hours) {
		GlindaTimePoint before = now.minus(hours(hours));
		GlindaTimePoint within = now.minus(hours(hours - 1));

		assertThat(before, atLeast(hours(hours)).before(now));
		assertThat(within, not(atLeast(hours(hours)).before(now)));
	}

	@Test
	public void atLeastToString() {
		assertThat(atLeast(hours(3)).before(jan1).toString(),
				is("at least 3 hours before 2007-01-01 12:00:00"));
	}

	@Theory
	public void allDataInAtLeastToString(int hours, GlindaTimePoint date) {
		assertThat(atLeast(days(hours)).before(date).toString(),
				containsStrings(hours, date.getYear()));
	}

	@Test
	public void atMostToString() {
		assertThat(atMost(minutes(5)).after(jan1).toString(),
				is("at most 5 minutes after 2007-01-01 12:00:00"));
	}

	@Theory
	public void minutesWork(int n) {
		assertThat(jan1.plus(minutes(n)), atLeast(minutes(n)).after(jan1));
	}

	@Theory
	public void lessThanWorks(GlindaTimePoint date, int small, int big) {
		assumeTrue(small < big);
		assertThat(date.plus(minutes(small)), lessThan(minutes(big))
				.after(date));
	}
}
package test.net.saff.glinda.units.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import net.saff.glinda.time.DayOfWeek;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;
import net.saff.glinda.time.Never;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

import com.domainlanguage.time.Duration;

@RunWith(Theories.class) public class HowDoesGlindaTimePoint extends
		LoqBookDataPoints {
	@DataPoint public static GlindaTimePoint never = Never.INSTANCE;

	@Test(expected = None.class) public void parseADateWithDots() {
		new GlindaTimePointParser().parse("2007.01.01 01:00:00");
	}

	@Test public void displayAsString() {
		assertThat(new GlindaTimePointParser().parse("2007-01-01 01:00:00").toString(),
				is("2007-01-01 01:00:00"));
	}

	@Test(timeout = 100) public void parseQuickly() {
		for (int i = 0; i < 10000; i++)
			new GlindaTimePointParser().parse("2007-01-01 01:00:00");
	}

	@Test public void getDayOfWeek() {
		assertThat(DayOfWeek.from(new GlindaTimePointParser().parse("1998-01-03 12:00:00")),
				is(DayOfWeek.Saturday));
	}

	@Theory public void notEqualLaterTime(GlindaTimePoint now, Duration duration) {
		assumeThat(duration, not(Duration.NONE));
		assumeThat(now, not(Never.INSTANCE));
		assertThat(now.plus(duration), not(now));
	}

	@Test(expected = None.class) public void equalityThrowsNoException(
			GlindaTimePoint a, GlindaTimePoint b) {
		a.equals(b);
	}

	@Theory public void maintainZeroSeconds(GlindaTimePoint now, int hourOfDay) {
		assumeThat(now, not(Never.INSTANCE));
		assertThat(now.moveToHourOfDay(hourOfDay).getSeconds(), is(0));
	}

	@Test public void gripeAboutNullStrings() {
		assertThat(new GlindaTimePointParser().parse(((String)null)), nullValue());
	}
}

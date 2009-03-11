package test.net.saff.glinda.units.projects.routines;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import net.saff.glinda.projects.routines.RelativeTime;
import net.saff.glinda.time.DayOfWeek;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class HowDoesRelativeTime extends
		LoqBookDataPoints {
	@DataPoint public static RelativeTime rtime = new RelativeTime("Sunday");

	@Theory public void parseMultipleWeekdays(GlindaTimePoint now) {
		GlindaTimePoint sundayAt6 = new RelativeTime("Sunday at 6pm")
				.after(now);
		GlindaTimePoint mondayAt5 = new RelativeTime("Sunday and Monday at 5pm")
				.after(sundayAt6);
		assertThat(DayOfWeek.from(mondayAt5), is(DayOfWeek.Monday));
	}

	@Theory public void onlyReturnNInstances(GlindaTimePoint now,
			RelativeTime time, int howMany) {
		assumeTrue(howMany <= 7);
		assertThat(time.nextNInstances(now, howMany).size(), is(howMany));
	}

	@Test public void interpretMonthlyRoutinesAfter() {
		assertThat(new RelativeTime("each month on the 13th at 3pm").after(
				GlindaTimePoint.now()).getDayOfMonth(), is(13));
	}
}

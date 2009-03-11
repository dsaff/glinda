package test.net.saff.glinda.units.projects.routines;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.saff.glinda.projects.routines.RelativeTimeUnit;
import net.saff.glinda.projects.routines.TimeDirection;
import net.saff.glinda.time.DayOfWeek;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class HowDoesRelativeTimeUnit extends
		LoqBookDataPoints {
	@Theory public void findDayOfWeekInFlatWeekdayString(GlindaTimePoint now) {
		assertThat(DayOfWeek.from(RelativeTimeUnit.dayOfWeek.push(now,
				TimeDirection.backward, "Wednesday")), is(DayOfWeek.Wednesday));
	}
}

package test.net.saff.glinda.units.time;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.minutes;
import static com.domainlanguage.time.Duration.weeks;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.saff.glinda.projects.routines.DurationString;

import org.junit.Test;

public class HowDoesDurationString {	
	@Test public void handleMinutes() {
		assertThat(new DurationString("3 minutes").parse(), is(minutes(3)));
	}	
	
	@Test public void handleDays() {
		assertThat(new DurationString("3 days").parse(), is(days(3)));
	}
	
	@Test public void handleOthers() {
		assertThat(new DurationString("3m").parse(), is(minutes(3)));
		assertThat(new DurationString("3w").parse(), is(weeks(3)));
		assertThat(new DurationString("3d").parse(), is(days(3)));
		assertThat(new DurationString("3 weeks").parse(), is(weeks(3)));
	}
}

package test.net.saff.glinda.units.book;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.saff.glinda.book.StaticParser;
import net.saff.glinda.book.WaitTime;
import net.saff.glinda.time.DayOfWeek;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;

import java.text.ParseException;

public class HowDoesStaticParser {
	@Test public void parseNextWednesday() throws ParseException {
		WaitTime wtime = (WaitTime) new StaticParser().parse(
				"next Wednesday at 2pm", WaitTime.class);
		assertThat(DayOfWeek.from(wtime
				.timeToStopWaiting(GlindaTimePoint.now())),
				is(DayOfWeek.Wednesday));
	}
}

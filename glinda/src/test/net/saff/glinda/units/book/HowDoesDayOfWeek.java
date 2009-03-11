/**
 * 
 */
package test.net.saff.glinda.units.book;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.saff.glinda.time.DayOfWeek;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class HowDoesDayOfWeek extends
		LoqBookDataPoints {
	@Test public void getCorrectInt() {
		assertThat(DayOfWeek.Sunday.asInt(), is(1));
	}
	
	@Test public void computeDaysAfter() {
		assertThat(DayOfWeek.Monday.daysAfter(DayOfWeek.Tuesday), is(6));
	}
	
	@Theory public void computeAddition(DayOfWeek day, int delta) {
		assertThat(day.plus(delta).daysAfter(day), is(delta % 7));
	}
}
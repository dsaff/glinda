/**
 * 
 */
package test.net.saff.glinda.units.time;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.Calendar;

import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;

public class TestDateUnderstanding {
	@Test
	public void characterizeDefaultDateFormat() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1);
		assertThat(GlindaTimePoint.from(calendar).longString(),
				containsString("2007-01-01"));
	}
}
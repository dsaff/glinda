package test.net.saff.glinda.units.projects;

import static org.junit.Assert.fail;
import net.saff.glinda.projects.goals.TimedValue;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class HowDoesValueAtTime extends
		LoqBookDataPoints {
	@Theory public void gripeAboutNullValue(GlindaTimePoint time) {
		try {
			new TimedValue(null, time);
			fail("Should have thrown exception");
		} catch (NullPointerException e) {
			// success!
		}
	}
}

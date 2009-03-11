/**
 * 
 */
package test.net.saff.glinda.units.book;

import static org.junit.Assert.assertTrue;
import net.saff.glinda.projects.routines.RoutineSpecification;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesRoutineSpecification extends LoqBookDataPoints {
	@Theory
	public void computeAnInstanceAfterNow(GlindaTimePoint now,
			RoutineSpecification specification) {
		assertTrue(specification.firstInstanceAfter(now).isAfter(now));
	}
}
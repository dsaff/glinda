package test.net.saff.glinda.units;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;
import net.saff.glinda.ideas.correspondent.Question;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.goals.GoalValue;
import net.saff.glinda.projects.goals.TimedValue;
import net.saff.stubbedtheories.ParameterTypes;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class) public class HowDoAllObjects {
	@DataPoints public static Object[] objects = { TimedValue.NULL, 3,
			new Question("Hello"), GoalValue.absolute(3.1),
			GoalValue.absolute(3.1), new BracketedString("name"),
			new BracketedString("name"), new ParameterTypes() };

	@Theory public void neverThrowExceptionOnEquals(Object a, Object b) {
		// expect no exception
		assumeNotNull(a);
		a.equals(b);
	}

	@Theory public void notEqualNull(Object a) {
		// expect no exception
		assumeNotNull(a);
		assertFalse(a.equals(null));
	}

	@Theory public void equalObjectsHaveEqualHashcodes(Object a, Object b) {
		assumeTrue(a.equals(b));
		assertTrue(a.hashCode() == b.hashCode());
	}
}

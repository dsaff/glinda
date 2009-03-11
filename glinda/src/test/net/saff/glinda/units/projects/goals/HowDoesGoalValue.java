package test.net.saff.glinda.units.projects.goals;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import net.saff.glinda.projects.goals.GoalValue;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesGoalValue extends LoqBookDataPoints {
	@Test(expected=IllegalStateException.class) public void preventValueAccessWhenNull() {
		GoalValue.NULL.asDouble();
	}
	
	@Theory public void onlyEqualSameNumber(int x, int y) {
		assumeThat(x, not(y));
		assertThat(GoalValue.absolute(x), not(GoalValue.absolute(y)));
	}
}

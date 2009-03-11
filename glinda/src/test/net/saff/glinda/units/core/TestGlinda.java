/**
 * 
 */
package test.net.saff.glinda.units.core;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import net.saff.glinda.book.GoingConcerns;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

@RunWith(Theories.class)
public class TestGlinda extends CondCorrespondentOnDiskTest {
	@DataPoint
	public static String GOAL_NAME = "MessyRooms";
	@DataPoint
	public static String GOAL_NAME2 = "DishLoads";

	@Theory
	public void payAttentionToLoqBook(String goalName) {
		r("#startGoal " + goalName);
		assertThat(output(GoingConcerns.withDefaults(), "goals"),
				containsString(goalName));
	}

	@Theory
	public void bucketizeWatchesIdeaNames(String idea) {
		r("#idea " + idea);
		alwaysAnswer("day");
		assertThat(output(GoingConcerns.withDefaults(), "bucketize"),
				containsString(idea));
	}
}
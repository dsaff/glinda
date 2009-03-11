/**
 * 
 */
package test.net.saff.glinda.user.c_goals;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class HowDoesGoalsOutput extends CondCorrespondentOnDiskTest {
	@Test
	public void displayAllGoals() {
		r("#startGoal Glinda\n");
		r("#startGoal Tinman\n");
		run("goals");
		w("goals:");
		w("Glinda");
		w("Tinman");
	}

	@Test public void ignoreColonsInInput() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms: 1");
		r("#NOW 01-02 12:00");
		r("#track MessyRooms 2");
		run("goals");
		w("goals:");
		w("MessyRooms");
	}

	@Test public void acceptBracketsInGoalName() {
		r("#NOW 07-01 12:00");
		r("#track [Messy rooms] 3");
		nowIs("07-05 12:00");
		run("goals");
		w("goals:");
		w("[Messy rooms]");
	}
}
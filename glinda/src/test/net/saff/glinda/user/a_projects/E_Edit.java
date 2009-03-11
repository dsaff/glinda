package test.net.saff.glinda.user.a_projects;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class E_Edit extends CondCorrespondentOnDiskTest {
	@Test public void changeGoalName() {
		r("#NOW 07-01 12:00");
		r("#track MessyRooms 3");
		r("#edit MessyRooms CleanRooms");
		runAtTime("07-05 12:00", "goalStatus", "CleanRooms");
		w("goalStatus: ! CleanRooms (4 days since last track)");
	}
	
	@Test public void changeProjectName() {
		r("#NOW 07-01 12:00");
		r("#startProject Jump");
		r("#edit Jump Leap");
		runAtTime("07-05 12:00", "status");
		w("status:");
		w("! p:Leap");
	}
}
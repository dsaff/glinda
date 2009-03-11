package test.net.saff.glinda.user.c_goals;

import net.saff.glinda.projects.goals.Goal;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class HowDoesExternalizeOutput extends CondCorrespondentOnDiskTest {
	@Test public void displayGoalData() {
		addImport(Goal.class, "set");
		addImport(Goal.class, "externalize");
		r("#NOW 01-01 12:00");
		r("#startGoal MessyRooms");
		r("#set MessyRooms compareVelocity=true");
		run("externalize", "MessyRooms");
		w("externalize:");
		w("#startGoal MessyRooms");
		w("#set MessyRooms compareVelocity=true");
		w("#set MessyRooms comparisonTimeframe=23 hours");
		w("#set MessyRooms moreIsBetter=false");
	}
}

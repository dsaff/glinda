/**
 * 
 */
package test.net.saff.glinda.user.b_links.a_basics;

import net.saff.glinda.projects.core.Project;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class A_NextSteps extends CondCorrespondentOnDiskTest {
	@Test
	public void postponeParent() {
		r("#startProject HangPicture");
		r("#nextStep HangPicture [find a nail]");

		runStatus();
		w("> p:HangPicture (next step: p:[find a nail])");
		w("! p:[find a nail]");
	}

	@Test
	public void allowTwoParentsForOneChild() {
		r("#startProject HangPicture");
		r("#startProject FixFloor");
		r("#nextStep HangPicture [find a nail]");
		r("#nextStep FixFloor [find a nail]");

		runStatus();
		w("> p:HangPicture (next step: p:[find a nail])");
		w("> p:FixFloor (next step: p:[find a nail])");
		w("! p:[find a nail]");
	}

	@Test
	public void displayParents() {
		r("#startProject FixFloor");
		r("#nextStep FixFloor CleanFloor");
		r("#nextStep CleanFloor FindMop");

		runStatus("-actionItemsFirst", "-withTimes", "-withParents");
		w("! p:FindMop <= CleanFloor <= FixFloor");
	}

	@Test
	public void displaySoftParents() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#nextStep FixFloor CleanFloor");
		r("#priorTo CleanFloor FindMop");
		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withTimes", "-withParents");
		w("! p:FindMop <- CleanFloor <= FixFloor");
	}

	@Test
	public void onlyDisplayDirectlyBlockedParents() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#priorTo FixFloor FindMop");
		r("#nextStep FixFloor CleanFloor");
		r("#priorTo CleanFloor FindMop");
		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withTimes", "-withParents");
		w("! p:FindMop <- CleanFloor <= FixFloor");
	}

	@Test
	public void onlyDisplayActionableParents() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep FixFloor FindMop");
		r("#priorTo CleanFloor FindMop");
		r("#wait CleanFloor 1d");
		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withTimes", "-withParents");
		w("! p:FindMop <= FixFloor");
	}

	@Test
	public void notShowOnGoalWhenChildIsDone() {
		r("#now 2007-01-01 12:00:00");
		r("#track MessyRooms 0");
		r("#now 2007-01-02 12:00:00");
		r("#track MessyRooms 1");

		r("#nextStep MessyRooms CleanFloor");
		r("#done CleanFloor");
		nowIs("2007-01-02 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! MessyRooms (was 0, is 1)");
	}

	@Test
	public void showAllParents() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep DoDishes Kitchen");
		r("#nextStep BakeBread Kitchen");
		r("#trip Kitchen");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:Kitchen");
		w("  <= DoDishes");
		w("  <= BakeBread");
		w("> p:DoDishes (next step: p:Kitchen)");
		w("> p:BakeBread (next step: p:Kitchen)");
	}

	@Test
	public void hideParentsOfNonTrips() {
		r("#now 2007-01-01 12:00:00");
		r("#nextStep DoDishes Kitchen");
		r("#nextStep BakeBread Kitchen");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:Kitchen <=* DoDishes");
		w("> p:DoDishes (next step: p:Kitchen)");
		w("> p:BakeBread (next step: p:Kitchen)");
	}

	@Test
	public void registerAsBlockingRequirement() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo DoDishes BuyDishwasher");
		r("#nextStep DoDishes Kitchen");
		r("#wait BuyDishwasher 10d");
		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:Kitchen <= DoDishes");
		w("> p:DoDishes (next step: p:Kitchen)");
		w("> p:BuyDishwasher (waiting until 2007-01-11 12:00:00)");
	}
}
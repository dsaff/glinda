/**
 * 
 */
package test.net.saff.glinda.user.b_links.a_basics;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class B_2_MultipleChildren extends CondCorrespondentOnDiskTest {
	@Test public void allowMultipleChildren() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#priorTo FixFloor GetHammer");
		r("#wait CleanFloor 1d");
		r("#wait GetHammer 1d");
		nowIs("2007-01-01 12:00:00");

		runStatus();
		w("! p:FixFloor");
		w("  [-> CleanFloor]");
		w("  [-> GetHammer]");
	}

	@Test public void notEffectDeprioritizedChildrenOnOtherBranches() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#priorTo FixFloor FindMop");
		r("#priorTo FindMop SearchKitchen");
		r("#priorTo FindMop SearchPantry");
		r("#wait SearchKitchen 1d");
		r("#wait CleanFloor 1d");
		nowIs("2007-01-01 12:00:00");

		runStatus();
		w("! p:SearchPantry");
		w("> p:FixFloor (first: p:FindMop until 2007-01-02 12:00:00)");
		w("> p:CleanFloor (waiting until 2007-01-02 12:00:00)");
		w("> p:FindMop (first: p:SearchPantry until 2007-01-02 12:00:00)");
		w("> p:SearchKitchen (waiting until 2007-01-02 12:00:00)");
	}

	private void runStatus() {
		run("status", "-actionItemsFirst", "-showFullTimes");
		w("status:");
	}
}
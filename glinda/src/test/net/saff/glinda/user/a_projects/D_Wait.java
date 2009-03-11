/**
 * 
 */
package test.net.saff.glinda.user.a_projects;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class D_Wait extends CondCorrespondentOnDiskTest {
	@Test public void affectStatusReport() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startProject MakeTea");
		r("#wait MakeTea 1 hour");
		nowIs("2007-01-02 12:01:00");

		run("status");
		w("status:");
		w("> p:MakeTea (waiting until 2007-01-02 13:00:00)");
	}

	@Test public void acceptAbbreviations() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startProject MakeTea");
		r("#wait MakeTea 1h");
		nowIs("2007-01-02 12:01:00");

		run("status");
		w("status:");
		w("> p:MakeTea (waiting until 2007-01-02 13:00:00)");
	}

	@Test public void affectHardParents() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject HangPicture");
		r("#startProject FixFloor");
		r("#nextStep HangPicture [find a nail]");
		r("#wait [find a nail] 1 hour");
		r("#nextStep FixFloor [find a nail]");
		nowIs("2007-01-01 12:00:00");

		run("status");
		w("status:");
		w("> p:HangPicture (next step: p:[find a nail])");
		w("> p:FixFloor (next step: p:[find a nail])");
		w("> p:[find a nail] (waiting until 2007-01-01 13:00:00)");
	}

	@Test public void handleMultiWordProjects() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject [do stuff]");
		r("#wait [do stuff] 1 hour");
		nowIs("2007-01-01 12:00:00");

		run("status");
		w("status:");
		w("> p:[do stuff] (waiting until 2007-01-01 13:00:00)");
	}

	@Test public void preventSoftParentHiding() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#wait CleanFloor 2 days");
		nowIs("2007-01-01 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("! p:FixFloor [-> CleanFloor]");
		w("> p:CleanFloor (waiting until 2007-01-03 12:00:00)");
	}

	@Test public void preventSoftAncestorHiding() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#nextStep CleanFloor FindMop");
		r("#wait FindMop 2 days");
		nowIs("2007-01-01 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("! p:FixFloor [-> CleanFloor => FindMop]");
		w("> p:CleanFloor (next step: p:FindMop)");
		w("> p:FindMop (waiting until 2007-01-03 12:00:00)");
	}

	@Test public void parseAbsoluteTimes() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#wait FixFloor 2pm");
		nowIs("2007-01-01 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("> p:FixFloor (waiting until 2007-01-01 14:00:00)");
	}

	@Test public void parseNextWednesday() {
		// This is a Monday
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#wait FixFloor next Wednesday at 2pm");
		nowIs("2007-01-01 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("> p:FixFloor (waiting until 2007-01-10 14:00:00)");
	}

	@Test public void handleWeekdayWithOutTime() {
		// This is a Monday
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#wait FixFloor next Wednesday");
		nowIs("2007-01-01 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("> p:FixFloor (waiting until 2007-01-10 00:00:00)");
	}
}
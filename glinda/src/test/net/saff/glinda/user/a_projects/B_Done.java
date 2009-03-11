/**
 * 
 */
package test.net.saff.glinda.user.a_projects;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class B_Done extends CondCorrespondentOnDiskTest {
	@Test public void removeProjectsFromStatusOutput() {
		r("#startProject Shrinkage");
		r("#done Shrinkage");

		run("status");
		w("status:");
	}

	@Test public void releaseDependentProjects() {
		r("#now 1998-01-03 12:00:00");
		r("#startProject HangPicture");
		r("#nextStep HangPicture [find a nail]");
		r("#done [find a nail]");

		run("status");
		w("status:");
		w("! p:HangPicture");
	}

	@Test public void removeParentFromPicture() {
		r("#now 1998-01-03 12:00:00");
		r("#startProject HangPicture");
		r("#nextStep [find a nail] HangPicture");
		r("#done [find a nail]");

		run("status", "-withParents");
		w("status:");
		w("! p:HangPicture");
	}

	@Test public void removeParentFromMulitpleChildren() {
		r("#now 1998-01-03 12:00:00");
		r("#nextStep FindANail HangPicture");
		r("#nextStep FindANail BeGoofy");
		r("#done FindANail");

		run("status", "-withParents");
		w("status:");
		w("! p:HangPicture");
		w("! p:BeGoofy");
	}
}
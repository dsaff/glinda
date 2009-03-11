/**
 * 
 */
package test.net.saff.glinda.user.a_projects;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class H_SetGlobal extends CondCorrespondentOnDiskTest {
	@Test public void affectDefaultExpiration() {
		r("#setGlobal defaultLinkExpiration=2 days");
		r("#now 2007-01-01 12:00:00");
		r("#startProject HangPicture");
		r("#priorTo HangPicture FindANail");
		nowIs("2007-01-02 11:00:00");

		run("status");
		w("status:");
		w("> p:HangPicture (first: p:FindANail until 01-03 12:00)");
		w("! p:FindANail");
	}
}
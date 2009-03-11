/**
 * 
 */
package test.net.saff.glinda.user.b_links.b_harden;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class A_MakesHardLinks extends CondCorrespondentOnDiskTest {
	@Test
	public void makeSoftLinksIntoHardLinks() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#harden A");
		nowIs("2007-01-01 12:00:01");

		run("status", "-actionItemsFirst", "-withParents");
		w("status:");
		w("! p:B <= A");
		w("> p:A (next step: p:B)");
	}
}
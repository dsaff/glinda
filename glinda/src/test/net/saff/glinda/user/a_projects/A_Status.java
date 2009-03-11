package test.net.saff.glinda.user.a_projects;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class A_Status extends CondCorrespondentOnDiskTest {
	@Test
	public void showActionItemsFirst() {
		r("#startProject HangPicture");
		r("#startProject FixFloor");
		r("#nextStep HangPicture [find a nail]");
		r("#nextStep FixFloor [find a nail]");

		runStatus("-actionItemsFirst");
		w("! p:[find a nail]");
		w("> p:HangPicture (next step: p:[find a nail])");
		w("> p:FixFloor (next step: p:[find a nail])");
	}

	@Test
	public void readMultipleFlags() {
		r("#startProject FixFloor");
		r("#nextStep FixFloor [find a nail]");

		runStatus("-actionItemsFirst", "-withTimes");
		w("! p:[find a nail]");
		w("> p:FixFloor (next step: p:[find a nail])");
	}
}

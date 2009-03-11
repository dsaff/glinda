package test.net.saff.glinda.user.h_ideas;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class TestIdeasOutput extends CondCorrespondentOnDiskTest {
	@Test
	public void ideas() {
		r("#idea Row my boat");
		r("#idea Fix London Bridge");
		r("#idea Cut off their tails");

		run("ideas");
		w("ideas:");
		w("[Row my boat]");
		w("[Fix London Bridge]");
		w("[Cut off their tails]");
	}
}

package test.net.saff.glinda.user.h_ideas;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class TestInterestOutput extends CondCorrespondentOnDiskTest {
	@Test
	public void dealsWithMissingInterest() {
		r("#interest Egon Ghost false");
		run("ideas");
		w("ideas:");
		w("Egon");
	}
}

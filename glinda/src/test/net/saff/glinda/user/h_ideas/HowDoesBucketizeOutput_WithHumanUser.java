package test.net.saff.glinda.user.h_ideas;

import static org.junit.Assume.assumeTrue;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.correspondent.UserKeyboardCorrespondent;

import org.junit.Test;

import test.net.saff.glinda.HumanUser;
import test.net.saff.glinda.testclasstypes.OnDiskLoqBookTest;

public class HowDoesBucketizeOutput_WithHumanUser extends OnDiskLoqBookTest {
	@Test public void recordDayBuckets() {
		r("#idea Please choose option 'day'");

		run("bucketize");
		w("bucketize:");
		w("#bucket [Please choose option 'day'] day");
	}

	@Test public void recordMonthBuckets() {
		r("#idea Please choose option 'month'");
		run("bucketize");
		w("bucketize:");
		w("#bucket [Please choose option 'month'] month");
	}

	@Override protected Correspondent getCorrespondent() {
		assumeTrue(HumanUser.isAvailable());
		return new UserKeyboardCorrespondent();
	}
}

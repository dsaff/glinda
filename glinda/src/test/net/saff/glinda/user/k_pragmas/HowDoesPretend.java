package test.net.saff.glinda.user.k_pragmas;

import static com.domainlanguage.time.Duration.days;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import net.saff.glinda.book.DurationWaitTime;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.book.WaitTime;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class HowDoesPretend extends CondCorrespondentOnDiskTest {
	@Test
	public void pretend() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject X");
		r("#startProject Y");
		r("#wait X 4pm");
		r("#pretendTime Y 5pm");
		nowIs("2007-01-01 12:00:00");
		run("status");
		w("status:");
		w("[=== pretending for Y: 5pm ===]");
		w("! p:X");
		w("> p:Y (currently pretending)");
	}
	
	@Test
	public void stopPretending() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject X");
		r("#startProject Y");
		r("#wait X 4pm");
		r("#pretendTime Y 5pm");
		r("#endPretend");
		nowIs("2007-01-01 12:00:00");
		run("status");
		w("status:");
		w("> p:X (waiting until 2007-01-01 16:00:00)");
		w("! p:Y");
	}
	
	@Test
	public void getUsedInHarden() {
		r("#now 2007-01-01 12:00:00");
		r("#startProject X");
		r("#startProject Y");
		r("#priorTo X Y");
		r("#pretendTime Z 3 days");
		r("#harden X");
		r("#endPretend");
		nowIs("2007-01-01 12:00:00");
		run("status", "-actionItemsFirst");
		w("status:");
		w("! p:Y");
		w("! p:Z");
		w("> p:X (next step: p:Y)");
	}

	@RunWith(Theories.class)
	public static class PretendUnits extends LoqBookDataPoints {
		private final Project project;
		private final WaitTime time;

		public PretendUnits(Project project, WaitTime time) {
			this.project = project;
			this.time = time;
		}

		@DataPoints
		public static WaitTime[] waitTimes = new WaitTime[] { new DurationWaitTime(
				days(3)) };

		@Theory
		public void includeThingWaitedFor(LoqBook book, GlindaTimePoint now) {
			book.now(now);
			book.pretendTime(project, time);
			assertThat(book.status(Flag.NONE), hasItem(both(
					containsString(project.toString())).and(
					containsString(time.toString()))));
		}

		@Theory
		public void retainOtherStatus(LoqBook book, BracketedString bstring,
				GlindaTimePoint now) {
			book.now(now);
			book.startProject(bstring);
			book.pretendTime(project, time);
			assertThat(book.status(Flag.NONE), hasItem(containsString(bstring
					.toString())));
		}
	}
}

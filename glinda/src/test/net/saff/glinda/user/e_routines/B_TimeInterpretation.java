/**
 * 
 */
package test.net.saff.glinda.user.e_routines;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.minutes;
import static net.saff.glinda.time.comparison.DurationComparison.lessThan;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import net.saff.glinda.projects.routines.RoutineSpecificationParser;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Enclosed.class)
public class B_TimeInterpretation {
	public static class OldTests extends CondCorrespondentOnDiskTest {
		@Test
		public void routinesWorkByDurationAndStartTime() {
			r("#NOW 2007-01-02 12:00:00");
			r("#startRoutine TakeOutRecycling each 2 weeks starting 11pm");
			r("#NOW 2007-01-03 12:00:10");
			r("#done TakeOutRecycling");
			nowIs("2007-01-03 12:00:20");

			run("status");
			w("status:");
			w("> r:TakeOutRecycling (until 2007-01-16 23:00:00)");
		}

		@Test
		public void routinesWorkByDurationAndStartTimeWithAt() {
			r("#NOW 2007-01-02 12:00:00");
			r("#startRoutine TakeOutRecycling each 2 weeks starting at 11pm");
			r("#NOW 2007-01-03 12:00:10");
			r("#done TakeOutRecycling");
			nowIs("2007-01-03 12:00:20");

			run("status");
			w("status:");
			w("> r:TakeOutRecycling (until 2007-01-16 23:00:00)");
		}

		@Test
		public void interpretMultipleWorkdays() {
			r("#NOW 1998-01-03 12:00:00");
			r("#startRoutine TakeOutRecycling each Tuesday, Monday, and Wednesday at 12pm");
			r("#done TakeOutRecycling");
			nowIs("1998-01-03 12:00:20");

			run("status");
			w("status:");
			w("> r:TakeOutRecycling (until 1998-01-05 12:00:00)");
		}

		@Test
		public void interpretMonthlyRoutine() {
			r("#NOW 1998-01-03 12:00:00");
			r("#startRoutine TakeOutRecycling each month on the 22nd at 3pm");
			r("#done TakeOutRecycling");
			nowIs("1998-01-03 12:00:20");

			run("status");
			w("status:");
			w("> r:TakeOutRecycling (until 1998-01-22 15:00:00)");
		}

		@Test
		public void interpretTwelvePm() {
			r("#NOW 2007-01-02 12:00:00");
			r("#startRoutine TakeOutRecycling each day at 12pm");
			nowIs("2007-01-03 12:00:20");

			run("status");
			w("status:");
			w("! r:TakeOutRecycling (since 2007-01-03 12:00:00)");
		}

		@Test
		public void interpretTwelveAm() {
			r("#NOW 2007-01-02 12:00:00");
			r("#startRoutine TakeOutRecycling each day at 12am");
			nowIs("2007-01-03 12:00:20");

			run("status");
			w("status:");
			w("! r:TakeOutRecycling (since 2007-01-03 00:00:00)");
		}

		@Test
		public void workWithNextWednesday() {
			r("#NOW 2008-01-30 09:00:52");
			r("#startRoutine PutOutOrganicsBin each 2 weeks starting next Wednesday at 5am");
			nowIs("2008-01-31 09:00:00");

			run("status");
			w("status:");
			w("> r:PutOutOrganicsBin (until 2008-02-13 05:00:00)");
		}

		@Test
		public void workWithNextWednesdayAfterAWhile() {
			r("#NOW 2008-01-30 09:00:52");
			r("#startRoutine PutOutOrganicsBin each 2 weeks starting next Wednesday at 5am");
			nowIs("2008-02-14 09:00:00");

			run("status");
			w("status:");
			w("! r:PutOutOrganicsBin (since 2008-02-13 05:00:00)");
		}
	}

	@RunWith(Enclosed.class)
	public static class WorkWithYesterday {
		public static class User extends CondCorrespondentOnDiskTest {
			@Test
			public void workWithYesterday() {
				r("#NOW 2008-01-30 09:00:52");
				r("#startRoutine PutOutOrganicsBin each 2 days starting yesterday at 5am");
				nowIs("2008-01-30 09:00:53");

				run("status");
				w("status:");
				w("! r:PutOutOrganicsBin (since 2008-01-29 05:00:00)");
			}
		}

		@RunWith(Theories.class)
		public static class Unit extends LoqBookDataPoints {
			@Theory
			public void anyTime(int hour, GlindaTimePoint now) {
				assumeTrue(hour > 0);
				assumeTrue(hour <= 12);
				GlindaTimePoint startTime = new RoutineSpecificationParser()
						.startTime(now, "yesterday at " + hour + "am");
				assertThat(startTime.getHourOfDay(), is(hour));
				assertThat(startTime, lessThan(days(2)).before(now));
			}
		}
	}

	@RunWith(Enclosed.class)
	public static class WorkWithExplicitDates {
		public static class User extends CondCorrespondentOnDiskTest {
			@Test
			public void workWithExplicitDates() {
				r("#NOW 2008-01-30 09:00:52");
				r("#startRoutine PutOutOrganicsBin each 2 days starting Feb 5 at 5am");
				nowIs("2008-01-30 09:00:53");

				run("status");
				w("status:");
				w("> r:PutOutOrganicsBin (until 2008-02-05 05:00:00)");
			}
		}

		@RunWith(Theories.class)
		public static class Unit extends LoqBookDataPoints {
			@Theory
			public void dateStringRoundtrip(GlindaTimePoint now) {
				assertThat(new GlindaTimePointParser().moveToDate(now, now
						.asNiceDateString()), lessThan(minutes(1)).before(now));
			}

			@Theory
			public void dateStringRoundtripInParser(GlindaTimePoint now,
					GlindaTimePoint other) {
				assertThat(new RoutineSpecificationParser().startTime(now,
						other.asNiceDateString()).getDayOfMonth(), is(other
						.getDayOfMonth()));
			}
		}
	}
}

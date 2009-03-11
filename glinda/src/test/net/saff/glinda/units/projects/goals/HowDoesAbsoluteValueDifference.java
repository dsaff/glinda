package test.net.saff.glinda.units.projects.goals;

import static java.util.Arrays.asList;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.goals.AbsoluteValueDifference;
import net.saff.glinda.projects.goals.GoalComparisonInterval;
import net.saff.glinda.projects.goals.GoalSettings;
import net.saff.glinda.projects.goals.GoalValue;
import net.saff.glinda.projects.goals.TimedValue;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

import com.domainlanguage.time.Duration;

@RunWith(Theories.class) public class HowDoesAbsoluteValueDifference extends
		LoqBookDataPoints {
	@DataPoints public static TimedValue[] valuesAtTime = { null };
	@DataPoints public static StatusRequest[] contexts = { null };

	@DataPoints public static AbsoluteValueDifference[] differences = { new AbsoluteValueDifference(
			TimedValue.NULL, new TimedValue(GoalValue.absolute(0),
					GlindaTimePoint.now()), GoalSettings.defaults(),
			new GoalComparisonInterval(GlindaTimePoint.now())) };

	@Theory public void askForNewNotificationWhenNowIsNull(Duration duration) {
		assertThat(new AbsoluteValueDifference(TimedValue.NULL,
				TimedValue.NULL, new GoalSettings(false, false, duration),
				new GoalComparisonInterval(GlindaTimePoint.now())).nextTrack(),
				containsString("now"));
	}

	@Theory public void complainAboutNullYesterdayValue(TimedValue now,
			GlindaTimePoint time) {
		try {
			new AbsoluteValueDifference(null, now, GoalSettings.defaults(),
					new GoalComparisonInterval(time));
			fail("should have thrown exception");
		} catch (NullPointerException e) {
			// success!
		}
	}

	@Theory public void alwaysShowNextTrack(AbsoluteValueDifference avd) {
		assertThat(asList(avd.comments(true, null, null)),
				hasItem(containsString("next track")));
	}
}

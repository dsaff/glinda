package test.net.saff.glinda.units.projects.goals;

import static com.domainlanguage.time.Duration.days;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import net.saff.glinda.projects.goals.GoalSettings;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.domainlanguage.time.Duration;

@RunWith(Theories.class) public class HowDoesGoalSettings {
	@DataPoint public static boolean TRUE = true;
	@DataPoint public static Duration ONE_DAY = days(1);
	
	@Theory public void notEqualDifferentSettings(boolean moreIsBetter,
			boolean compareVelocity, Duration timeframe) {
		GoalSettings settings = new GoalSettings(moreIsBetter, compareVelocity,
				timeframe);
		assertThat(settings, not(new GoalSettings(!moreIsBetter,
				compareVelocity, timeframe)));
		assertThat(settings, not(new GoalSettings(moreIsBetter,
				!compareVelocity, timeframe)));
		assertThat(settings, not(new GoalSettings(moreIsBetter,
				compareVelocity, timeframe.plus(days(1)))));
	}
}

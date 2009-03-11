package test.net.saff.glinda.units.projects.routines;

import static com.domainlanguage.time.Duration.hours;
import static com.domainlanguage.time.Duration.minutes;
import static org.junit.Assert.assertFalse;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.routines.RelativeTime;
import net.saff.glinda.projects.routines.Routine;
import net.saff.glinda.projects.routines.RoutineSpecificationParser;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesRoutine extends LoqBookDataPoints {
	@Theory
	public void blockParents(GlindaTimePoint now) {
		GlindaTimePoint at7pm = new RelativeTime("7pm").after(now);

		Project routine = Routine
				.withoutListener(new RoutineSpecificationParser().parse2(
						new BracketedString("HaveDinner"), "each day at 6pm",
						GlindaTimePoint.now()));
		routine.done(at7pm, new RequirementGraph<Project>());

		Requirement<Project> link = Requirement.hardForever(null, routine, at7pm
				.plus(minutes(30)));
		GlindaTimePoint nowNow = at7pm.plus(hours(1));
		assertFalse(link.isExpiredOrDone(nowNow));
	}
}

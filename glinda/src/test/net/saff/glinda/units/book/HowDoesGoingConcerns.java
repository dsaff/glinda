package test.net.saff.glinda.units.book;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class HowDoesGoingConcerns extends
		LoqBookDataPoints {
	@DataPoint public static GoingConcerns DEFAULT = GoingConcerns.withDefaults();

	@DataPoint public static GoingConcerns concernWithGoal() {
		GoingConcerns gc = GoingConcerns.withDefaults();
		gc.startGoal(new BracketedString("Ability"));
		return gc;
	}

	@DataPoint public static String ABILITY = "Ability";

	@Theory public void neverReturnNullFromFindConcern(GoingConcerns gc,
			BracketedString name) {
		assumeNotNull(gc);
		assertThat(gc.findConcern(name), not(nullValue()));
	}

	@Theory public void alwaysClearConcernOnDone(GoingConcerns gc,
			BracketedString name, GlindaTimePoint now) {
		assumeNotNull(gc);
		gc.done(name, now, new RequirementGraph<Project>());
		assertTrue(gc.noSuchConcern(name));
	}
}

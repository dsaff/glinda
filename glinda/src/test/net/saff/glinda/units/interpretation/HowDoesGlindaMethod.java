package test.net.saff.glinda.units.interpretation;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesGlindaMethod extends LoqBookDataPoints {
	@Test
	public void distinguishUnequalMethods(
			TypeSpecificParserFactory arbitraryFactory)
			throws SecurityException, NoSuchMethodException {
		assertThat(GlindaMethod.forMethod(String.class.getMethod("toString"),
				arbitraryFactory), not(GlindaMethod.forMethod(String.class
				.getMethod("wait"), arbitraryFactory)));
	}
}

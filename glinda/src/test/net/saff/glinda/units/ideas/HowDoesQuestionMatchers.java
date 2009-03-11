package test.net.saff.glinda.units.ideas;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.lang.reflect.Method;

import net.saff.glinda.ideas.correspondent.QuestionMatchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


@RunWith(Theories.class) public class HowDoesQuestionMatchers {
	@DataPoints public static Method[] matcherMethods = QuestionMatchers.class
			.getMethods();

	@DataPoint public static Matcher<String> IS3 = is("3");
	@DataPoint public static Matcher<String> IS4 = is("4");

	@Theory public void describeDifferentMatchersDifferently(Method factory,
			Object val1, Object val2) throws Exception {
		assumeThat(val1, not(val2));
		assumeThat(factory.getAnnotation(Factory.class), notNullValue());

		Class<?>[] types = factory.getParameterTypes();
		assumeThat(types.length, is(1));
		assumeThat(val1, is(types[0]));
		assumeThat(val2, is(types[0]));

		assertThat(factory.invoke(null, val1).toString(), not(factory.invoke(
				null, val2).toString()));
	}
}


package test.net.saff.stubbedtheories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import net.saff.glinda.ideas.correspondent.Question;
import net.saff.stubbedtheories.ConstructorEquality;
import net.saff.stubbedtheories.ParameterTypes;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class) public class HowDoesConstructorEquality {
	@DataPoints public static Class<?>[] classes = { Question.class,
			ParameterTypes.class };
	@DataPoints public static Object[][] possibleParameters = {
			{ "a", new String[0] }, { "b", new String[0] },
			{ "a", new String[] { "c" } }, { new Class[0] },
			{ new Class[] { int.class } } };

	@Theory public void equalIffParametersEqual(Class<?> type,
			Object[] params1, Object[] params2) {
		assumeTrue(ConstructorEquality.class.isAssignableFrom(type));

		Constructor<?>[] constructors = type.getConstructors();
		assertThat(constructors.length, is(1));
		Constructor<?> constructor = constructors[0];

		Object obj1 = null, obj2 = null;
		try {
			obj1 = constructor.newInstance(params1);
			obj2 = constructor.newInstance(params2);
		} catch (Throwable e) {
			assumeNoException(e);
		}

		assertThat(obj1.equals(obj2), is(Arrays.deepEquals(params1, params2)));
	}
}

package test.net.saff.stubbedtheories;

import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import net.saff.stubbedtheories.guessing.MethodCall;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestMethodCall {
	@Test
	public void deepEqualsOnMethodCalls() throws SecurityException,
			NoSuchMethodException {
		Method method = toStringMethod();
		assertEquals(new MethodCall(method,
				new Object[] { new String[] { "a" } }), new MethodCall(method,
				new Object[] { new String[] { "a" } }));
	}

	@DataPoint
	public static Method toStringMethod() throws NoSuchMethodException {
		return Object.class.getMethod("toString");
	}

	@DataPoint
	public static Method compareTo() throws NoSuchMethodException {
		return Comparable.class.getMethod("compareTo", Object.class);
	}

	@DataPoint public static Object[] someArgs = new Object[]{1, "five"};
	
	@DataPoint public static int ZERO = 0;
	@DataPoint public static int ONE = 1;
	
	@Test
	public void toStringWorks() throws NoSuchMethodException {
		assertThat(new MethodCall(toStringMethod()).toString(),
				is("toString()"));
	}

	@Theory
	public void toStringIncludesMethodNameAndParameters(Method method,
			Object[] args, int index) {
		assertThat(new MethodCall(method, args).toString(), containsStrings(
				method.getName(), args[index].toString()));
	}
}

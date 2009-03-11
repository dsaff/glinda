package test.net.saff.glinda.units.projects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static test.net.saff.glinda.units.projects.InvocationAssertionBuilder.assertThatResult;

import java.util.ArrayList;

import org.junit.Test;

public class HowDoesInvocationAssertionBuilder {
	@Test public void assertThatResultFails() {
		boolean failed = false;
		try {
			assertThatResult(is(false)).from(new ArrayList<Object>()).isEmpty();
		} catch (AssertionError e) {
			failed = true;
		}
		assertTrue(failed);
	}
}

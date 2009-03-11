/**
 * 
 */
package test.net.saff.glinda.units.projects;

import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.lib.legacy.ClassImposteriser;

class InvocationAssertionBuilder<T> {
	private static class SourceAwareMatcher<T, U> extends BaseMatcher<T> {
		private final Matcher<T> matcher;
		private final Invocation invocation;
		private final U target;

		public SourceAwareMatcher(Matcher<T> matcher,
				Invocation invocation, U target) {
			this.matcher = matcher;
			this.invocation = invocation;
			this.target = target;
		}

		public boolean matches(Object arg0) {
			return matcher.matches(arg0);
		}

		public void describeTo(Description arg0) {
			arg0.appendDescriptionOf(matcher);
			arg0.appendText(" from invocation of ");
			arg0.appendText(invocation.getInvokedMethod().getName());
			arg0.appendValueList("(", ", ", ")", invocation
					.getParametersAsArray());
			arg0.appendText(" on ");
			arg0.appendValue(target);
		}
	}

	static <T> InvocationAssertionBuilder<T> assertThatResult(
			Matcher<T> matcher) {
		return new InvocationAssertionBuilder<T>(matcher);
	}

	private final Matcher<T> matcher;

	public InvocationAssertionBuilder(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	@SuppressWarnings("unchecked") public <U> U from(final U target) {
		return (U) ClassImposteriser.INSTANCE.imposterise(new Invokable() {
			public Object invoke(Invocation invocation) throws Throwable {
				Object result = invocation.applyTo(target);
				assertThat((T) result, new InvocationAssertionBuilder.SourceAwareMatcher<T, U>(
						matcher, invocation, target));
				return result;
			}
		}, target.getClass());
	}
}
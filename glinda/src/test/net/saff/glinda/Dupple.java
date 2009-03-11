package test.net.saff.glinda;

import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;

import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.internal.InvocationExpectation;
import org.jmock.internal.matcher.ParametersMatcher;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;

// TODO (Jun 16, 2008 7:13:33 PM): release Dupple as OS
public class Dupple {
	// TODO (Jun 16, 2008 8:17:54 PM): cheating
	private static Dupplery lastDupplery = null;

	// TODO (Jun 16, 2008 8:15:28 PM): in Mapple, Dupplery is not always a
	// Mockery
	public static class Dupplery {
		private List<Invocation> invocations = new ArrayList<Invocation>();

		public ServerSocketFactory recorder(
				@SuppressWarnings("unused") Class<ServerSocketFactory> class1) {
			// TODO Auto-generated method stub
			return ClassImposteriser.INSTANCE.imposterise(new Invokable() {
				public Object invoke(Invocation invocation) throws Throwable {
					invocations.add(invocation);
					// TODO Auto-generated method stub
					return null;
				}
			}, ServerSocketFactory.class);
		}

		public ServerSocketFactory assertCalled(
				@SuppressWarnings("unused") ServerSocketFactory factory) {
			// TODO (Jun 16, 2008 7:13:33 PM): release Dupple as OS
			return ClassImposteriser.INSTANCE.imposterise(new Invokable() {
				public Object invoke(Invocation invocation) throws Throwable {
					InvocationExpectation expectation = expectationFromInvocation(invocation);
					// TODO (Jun 16, 2008 8:01:49 PM): don't just match first
					if (!expectation.matches(invocations.get(0)))
						Assert.fail();
					return null;
				}
			}, ServerSocketFactory.class);
			// TODO Auto-generated method stub
		}

	}

	// TODO (Jun 16, 2008 7:55:15 PM): Use Google collections
	// TODO (Jun 16, 2008 7:56:36 PM): non-static
	// TODO (Jun 16, 2008 7:14:00 PM): generalize
	public static ServerSocketFactory recorder(Class<ServerSocketFactory> class1) {
		lastDupplery = new Dupplery();
		return lastDupplery.recorder(class1);
		// TODO (Jun 16, 2008 7:13:33 PM): release Dupple as OS
		// TODO (Jun 16, 2008 7:16:00 PM): remove suppress
		// TODO Auto-generated method stub
	}

	// TODO (Jun 16, 2008 7:14:00 PM): generalize
	public static ServerSocketFactory assertCalled(ServerSocketFactory factory) {
		return getDupplery(factory).assertCalled(factory);
	}

	private static Dupplery getDupplery(
			@SuppressWarnings("unused") ServerSocketFactory factory) {
		// TODO Auto-generated method stub
		return lastDupplery;
	}

	public static InvocationExpectation expectationFromInvocation(
			Invocation invocation) {
		InvocationExpectation expectation = new InvocationExpectation();
		expectation.setParametersMatcher(new ParametersMatcher(invocation
				.getParametersAsArray()));
		return expectation;
	}
}

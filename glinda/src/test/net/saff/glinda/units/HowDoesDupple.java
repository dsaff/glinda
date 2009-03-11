package test.net.saff.glinda.units;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;

import org.jmock.api.Expectation;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.Dupple;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

// TODO (Jun 16, 2008 7:48:36 PM): package too big

@RunWith(Theories.class)
public class HowDoesDupple extends LoqBookDataPoints {
	@Theory
	public void duppleRecorderIsRight(int x, int y) throws IOException {
		assumeThat(x, not(y));
		ServerSocketFactory factory = Dupple
				.recorder(ServerSocketFactory.class);
		factory.createServerSocket(x);
		try {
			Dupple.assertCalled(factory).createServerSocket(y);
		} catch (AssertionError e) {
			return;
		}
		fail("Should have thrown exception");
	}

	@Test
	public void invocationsMatchAsExpected() throws IOException {
		final List<Invocation> invocations = new ArrayList<Invocation>();
		ServerSocketFactory factory = ClassImposteriser.INSTANCE.imposterise(
				new Invokable() {
					public Object invoke(Invocation invocation)
							throws Throwable {
						invocations.add(invocation);
						return null;
					}
				}, ServerSocketFactory.class);
		factory.createServerSocket(100);
		factory.createServerSocket(200);
		Expectation e = Dupple.expectationFromInvocation(invocations.get(0));
		assertFalse(e.matches(invocations.get(1)));
	}
}

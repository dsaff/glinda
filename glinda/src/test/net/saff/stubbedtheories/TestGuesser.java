package test.net.saff.stubbedtheories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.util.List;

import net.saff.stubbedtheories.Stub;
import net.saff.stubbedtheories.StubbedTheories;
import net.saff.stubbedtheories.guessing.Guesser;
import net.saff.stubbedtheories.guessing.ReguessableValue;

import org.junit.Test;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.RunWith;

public class TestGuesser {
	@SuppressWarnings("unchecked") @Test public void guesserWorksOnAnyInterface()
			throws CouldNotGenerateValueException {
		Guesser<Comparable> guesser = new Guesser(Comparable.class);
		int firstGuess = guesser.getProxy().compareTo(null);
		List<ReguessableValue> potentials = guesser
				.reguesses(new AssumptionViolatedException(firstGuess, is(-1)));

		Comparable<Object> comparable = (Comparable<Object>) potentials.get(0)
				.getValue();
		assertThat(comparable.compareTo(null), is(-1));
	}

	@RunWith(StubbedTheories.class) public static class CanFigureOutMultipleCalls {
		@Theory public void twoCalls(@Stub Comparable<Object> comparable) {
			assumeThat(comparable.compareTo("a"), is(1));
			assumeThat(comparable.compareTo("b"), is(2));
			assertThat(comparable.compareTo("b"), is(2));
			assertThat(comparable.compareTo("a"), is(1));
		}
	}

	@Test public void guesserChainEventuallyDies() {

	}
}

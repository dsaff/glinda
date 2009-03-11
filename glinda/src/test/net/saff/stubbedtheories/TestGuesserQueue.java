package test.net.saff.stubbedtheories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import net.saff.stubbedtheories.guessing.Guesser;
import net.saff.stubbedtheories.guessing.GuesserQueue;
import net.saff.stubbedtheories.guessing.ReguessableValue;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

public class TestGuesserQueue {
	private static class AddingGuesser extends Guesser<Object> {
		private final int howMany;

		public AddingGuesser(int howMany) {
			super(Object.class);
			this.howMany = howMany;
		}

		@Override public List<ReguessableValue> reguesses(
				AssumptionViolatedException e) {
			List<ReguessableValue> list = new ArrayList<ReguessableValue>();
			for (int i = 0; i < howMany; i++)
				list.add(new AddingGuesser(howMany));
			return list;
		}
	}

	@Test public void updateOnlyWorksOnLastReturnedGuesser() {
		GuesserQueue q = new GuesserQueue();
		q.add(new AddingGuesser(1));
		q.add(new AddingGuesser(2));
		q.remove(0);
		q.update(new AssumptionViolatedException(1, is(0)));
		assertThat(q.size(), is(2));
	}
}

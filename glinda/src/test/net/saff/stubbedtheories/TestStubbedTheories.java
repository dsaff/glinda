package test.net.saff.stubbedtheories;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.stubbedtheories.Stub;
import net.saff.stubbedtheories.StubbedTheories;

import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

public class TestStubbedTheories {
	@RunWith(StubbedTheories.class)
	public static class AskADifferentQuestion {
		@Theory
		public void ask(@Stub
		Correspondent correspondent) {
			assumeThat(
					correspondent.getAnswer("What is five?", "four", "five"),
					is("five"));
		}
	}
}

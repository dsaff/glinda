package test.net.saff.glinda.units.interpretation;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import net.saff.glinda.interpretation.finding.CommandStrings;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesCommandStrings extends LoqBookDataPoints {
	@Test(expected = None.class) public void ignoreNullsWhenCompressingBrackets() {
		new CommandStrings(new String[] { null }).compressBrackets();
	}

	@Test public void includeSubbrackets() {
		CommandStrings compressBrackets = new CommandStrings(
				"interest",
				"[Get model train set? ([freecycleCambridgeMA] Cat Tunnel, Kids Jewelry, Model trains)] Agitar false")
				.compressBrackets();
		assertEquals(
				new CommandStrings(
						"interest",
						"[Get model train set? ([freecycleCambridgeMA] Cat Tunnel, Kids Jewelry, Model trains)]",
						"Agitar false"), compressBrackets);
	}

	@Test public void includeMultipleSubbrackets() {
		CommandStrings compressBrackets = new CommandStrings("before",
				"[la Nina] [la Pinta]").compressBrackets();
		assertEquals(new CommandStrings("before", "[la Nina]", "[la Pinta]"),
				compressBrackets);
	}
	
	@Theory public void onlyEqualEqualArrays(String s1, String s2) {
		assumeThat(s1, not(s2));
		assertThat(new CommandStrings(s1), not(new CommandStrings(s2)));
	}
}

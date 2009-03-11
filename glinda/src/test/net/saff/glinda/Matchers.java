package test.net.saff.glinda;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;
import net.saff.glinda.names.BracketedString;

import org.hamcrest.Matcher;


public class Matchers {

	public static Matcher<Iterable<String>> hasLineContaining(BracketedString bstring) {
		return Matchers.hasLineContaining(bstring.toString());
	}

	public static Matcher<Iterable<String>> hasLineContaining(String string) {
		return hasItem(containsString(string));
	}

}

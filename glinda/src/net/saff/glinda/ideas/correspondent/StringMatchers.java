package net.saff.glinda.ideas.correspondent;

import static org.hamcrest.CoreMatchers.allOf;

import java.util.ArrayList;

import org.hamcrest.Matcher;
import org.junit.matchers.JUnitMatchers;

public class StringMatchers {
	public static Matcher<String> containsString(Object obj) {
		return JUnitMatchers.containsString(String.valueOf(obj));
	}

	public static Matcher<String> containsStrings(Object... objs) {
		ArrayList<Matcher<? extends String>> matchers = new ArrayList<Matcher<? extends String>>();
		for (Object object : objs) {
			matchers.add(containsString(object));
		}
		return allOf(matchers);
	}
}

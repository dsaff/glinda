package net.saff.glinda.ideas.correspondent;

import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.hamcrest.CoreMatchers.anything;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class QuestionMatchers {
	@Factory public static Matcher<Question> isSomethingElse() {
		return anything();
	}

	@Factory public static Matcher<Question> hasOptionThat(
			final Matcher<String> optionMatcher) {
		return new BaseMatcher<Question>() {

			public void describeTo(Description d) {
				d.appendText("has option that ");
				d.appendDescriptionOf(optionMatcher);
			}

			public boolean matches(Object arg0) {
				Question q = (Question) arg0;
				String[] options = q.getOptions();
				for (String option : options)
					if (optionMatcher.matches(option))
						return true;
				return false;
			}

		};
	}

	@Factory public static Matcher<Question> hasPromptThat(
			final Matcher<String> promptMatcher) {
		return new BaseMatcher<Question>() {

			public void describeTo(Description d) {
				d.appendText("has prompt that ");
				d.appendDescriptionOf(promptMatcher);
			}

			public boolean matches(Object arg0) {
				Question q = (Question) arg0;
				return promptMatcher.matches(q.getPrompt());
			}
		};
	}
	
	@Factory public static Matcher<Question> isPrioritization() {
		return hasPromptThat(containsString("more"));
	}
}

/**
 * 
 */
package net.saff.glinda.ideas.correspondent;

import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.hamcrest.Matcher;

public class CondCorrespondent extends
		LinkedHashMap<Matcher<? super Question>, Matcher<String>> implements
		Correspondent {
	private static final long serialVersionUID = 1L;

	public String getAnswer(String question, String... options) {
		for (Entry<Matcher<? super Question>, Matcher<String>> pair : entrySet())
			if (pair.getKey().matches(new Question(question, options)))
				for (String option : options)
					if (pair.getValue().matches(option))
						return option;
		return null;
	}

	public AnswerBuilder ifQuestion(Matcher<? super Question> matcher) {
		return new AnswerBuilder(matcher);
	}

	public void alwaysChooseAnswerThat(Matcher<String> optionMatcher) {
		ifQuestion(notNullValue()).thenChooseAnswerThat(optionMatcher);
	}

	public class AnswerBuilder {
		private Matcher<? super Question> questionCriteria;

		public AnswerBuilder(Matcher<? super Question> matcher) {
			this.questionCriteria = matcher;
		}

		public void thenChooseAnswerThat(final Matcher<String> optionCriteria) {
			put(questionCriteria, optionCriteria);
		}

		public void thenChooseAnswerContaining(String string) {
			thenChooseAnswerThat(containsString(string));
		}
	}
}
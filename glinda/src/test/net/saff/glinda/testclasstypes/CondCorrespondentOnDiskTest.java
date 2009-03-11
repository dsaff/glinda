/**
 * 
 */
package test.net.saff.glinda.testclasstypes;

import static java.util.Arrays.asList;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.hasOptionThat;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.hasPromptThat;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.isPrioritization;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.isSomethingElse;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.both;

import java.util.ArrayList;

import net.saff.glinda.ideas.correspondent.CondCorrespondent;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.correspondent.Question;
import net.saff.glinda.ideas.correspondent.CondCorrespondent.AnswerBuilder;

import org.hamcrest.Matcher;
import org.junit.Before;

public class CondCorrespondentOnDiskTest extends OnDiskLoqBookTest {
	@Before public void setUpStartTime() {
		r("#now 1998-01-03 12:00:00");
	}
	
	private CondCorrespondent correspondent = new CondCorrespondent();

	@Override protected Correspondent getCorrespondent() {
		return correspondent;
	}

	protected AnswerBuilder ifQuestion(Matcher<Question> matcher) {
		return correspondent.ifQuestion(matcher);
	}

	protected void otherwiseChooseAnswerContaining(String string) {
		ifQuestion(isSomethingElse()).thenChooseAnswerContaining(string);
	}

	protected void alwaysAnswer(String string) {
		otherwiseChooseAnswerContaining(string);
	}

	protected AnswerBuilder ifQuestionContains(String string) {
		return ifQuestion(hasPromptThat(containsString(string)));
	}

	protected void markDoneWhenPossible(String ideaSubstring) {
		ifQuestion(
				both(isPrioritization()).and(
						hasOptionThat(containsString(ideaSubstring))))
				.thenChooseAnswerContaining("done");
		ifQuestion(
				both(hasPromptThat(containsString("done"))).and(
						hasOptionThat(containsString(ideaSubstring))))
				.thenChooseAnswerContaining(ideaSubstring);
	}

	protected void runAtTime(String timeString, String... command) {
		nowIs(timeString);
		run(command);
	}

	protected void runStatus(String... flags) {
		run(concat("status", flags));
		w("status:");
	}

	private String[] concat(String string, String[] flags) {
		ArrayList<String> list = new ArrayList<String>(asList(flags));
		list.add(0, string);
		return list.toArray(new String[0]);
	}

	protected void runStatusWithParents() {
		run("status", "-actionItemsFirst", "-withParents", "-showFullTimes");
		w("status:");
	}
}
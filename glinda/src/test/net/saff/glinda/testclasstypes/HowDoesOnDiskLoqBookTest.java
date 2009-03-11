package test.net.saff.glinda.testclasstypes;

import static net.saff.glinda.ideas.correspondent.QuestionMatchers.hasOptionThat;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.hasPromptThat;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.isSomethingElse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.both;

import java.lang.reflect.Method;

import net.saff.glinda.ideas.correspondent.Question;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class) public class HowDoesOnDiskLoqBookTest extends
		CondCorrespondentOnDiskTest {
	@DataPoints public static Question[] questions = { new Question("What?") };

	@DataPoints public static String[] strings = { "Lyra" };

	@DataPoints public static Method[] thisClassesMethods = HowDoesOnDiskLoqBookTest.class
			.getMethods();

	@Theory public void alwaysPassFirstIfSomethingElse(Question q) {
		assertThat(q, isSomethingElse());
	}

	@Theory public void matchAnswersToQuestions(String prompt, String answer) {
		ifQuestion(
				both(hasPromptThat(is(prompt))).and(hasOptionThat(is(answer))))
				.thenChooseAnswerThat(is(answer));

		assertThat(getCorrespondent().getAnswer(prompt, answer), is(answer));
	}

	@Test public void describeOptionMatchers() {
		assertThat(hasOptionThat(is("3")).toString(),
				is("has option that is \"3\""));
	}

	@Test public void describePromptMatchers() {
		assertThat(hasPromptThat(is("3")).toString(),
				is("has prompt that is \"3\""));
	}
}

package test.net.saff.glinda.user.h_ideas;

import static net.saff.glinda.ideas.correspondent.QuestionMatchers.hasPromptThat;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.matchers.JUnitMatchers.both;
import net.saff.glinda.ideas.Idea;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class TestEstimatizeOutput extends CondCorrespondentOnDiskTest {
	@Test public void forHappyPath() {
		r("#idea Nina");
		r("#idea Pinta");
		r("#idea Santa Maria");
		r("#idea Mayflower");
		r("#interest Nina Isabella true");
		r("#interest Pinta Isabella true");
		r("#interest [Santa Maria] Isabella true");
		r("#interest Mayflower Isabella false");

		incrementUntilEstimateReached("Nina", "1.0");
		incrementUntilEstimateReached("Pinta", "1.5");
		otherwiseChooseAnswerContaining("correct");

		run("estimatize", "interest=Isabella");

		w("estimatize:");
		w("#estimate Nina 1.0");
		w("#estimate Pinta 1.5");
		w("#estimate [Santa Maria] 0.5");
	}

	private void incrementUntilEstimateReached(String ideaName,
			String estimateString) {
		ifQuestion(
				hasPromptThat(both(containsString(ideaName)).and(
						not(containsString(estimateString)))))
				.thenChooseAnswerContaining("+");
	}

	@Test public void ignoresAlreadyEstimatedTasks() {
		addImport(Idea.class, "estimate");
		r("#idea Nina");
		r("#estimate Nina 1.0");
		r("#interest Nina Isabella true");

		alwaysAnswer("done");
		run("estimatize", "interest=Isabella");

		w("estimatize:");
		done();
	}
}

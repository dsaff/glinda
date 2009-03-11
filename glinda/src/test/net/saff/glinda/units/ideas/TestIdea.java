package test.net.saff.glinda.units.ideas;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import net.saff.glinda.ideas.Idea;
import net.saff.glinda.ideas.correspondent.CondCorrespondent;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.stubbedtheories.Capturer;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class TestIdea extends LoqBookDataPoints {
	@DataPoints public static Idea[] ideas = { new Idea("George") };

	@Theory public void interestIsKnownOnceNoted(Idea idea,
			String interestedParty, boolean value) {
		idea.interest(interestedParty, value);
		assertThat(idea.interestKnown(interestedParty), is(true));
	}

	@Theory public void usesIdeaNamesInQuestions(String ideaName) {
		Capturer<Correspondent> capturer = Capturer
				.forObject(immediatelyDone());
		new Idea(ideaName).askForEstimate(capturer.getProxy());
		assertThat(capturer.log(), containsString(ideaName));
	}

	@Theory public void usesIdeaNamesInEstimatization(String ideaName) {
		assertThat(new Idea(ideaName).askForEstimate(immediatelyDone()),
				containsString(ideaName));
	}

	private Correspondent immediatelyDone() {
		CondCorrespondent condCorrespondent = new CondCorrespondent();
		condCorrespondent.ifQuestion(notNullValue())
				.thenChooseAnswerContaining("c)");
		return condCorrespondent;
	}

	@Theory public void eventuallyBreaksInfiniteLoop() {
		Correspondent c = new Correspondent() {
			int answerCount = 0;

			public String getAnswer(String question, String... bucket) {
				if (answerCount++ > Idea.MAX_ANSWERS_PER_ESTIMATION_QUESTION * 2)
					throw new IllegalStateException();
				return "+) more";
			}
		};
		assertThat(new Idea("Pica").askForEstimate(c), containsString("Pica"));
	}
}

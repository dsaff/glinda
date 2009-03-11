package test.net.saff.glinda.units.ideas;

import static java.util.Arrays.asList;
import static net.saff.glinda.ideas.correspondent.QuestionMatchers.hasPromptThat;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import net.saff.glinda.ideas.Idea;
import net.saff.glinda.ideas.IdeaList;
import net.saff.glinda.ideas.correspondent.CondCorrespondent;
import net.saff.glinda.names.BracketedString;
import net.saff.stubbedtheories.Capturer;
import net.saff.stubbedtheories.StubbedTheories;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Enclosed.class) public class HowDoesIdeaListWhenBlank {
	@RunWith(Theories.class) public static class AndWithoutCorrespondent extends
			LoqBookDataPoints {
		private IdeaList list = new IdeaList();

		@Theory public void useIdeaNamesInBacklog(String ideaName,
				double estimate) {
			list.addWithEstimate(ideaName, estimate);
			assertThat(list.backlog(null, estimate), hasItem(containsStrings(
					ideaName, estimate)));
		}

		@Theory public void assumeHalfHourInBacklog(String ideaName) {
			list.add(ideaName);
			assertThat(list.backlog(null, 0.5), hasItem(containsString("0.5")));
		}

		@Theory public void correctlyReportEstimatesInBacklog(String ideaName,
				double estimate) {
			list.addWithEstimate(ideaName, estimate);
			assertThat(list.backlog(null, estimate - 1),
					hasItem(containsStrings(ideaName, estimate)));
		}
	}

	@RunWith(StubbedTheories.class) public static class AndWithCorrespondent
			extends LoqBookDataPoints {
		private CondCorrespondent correspondent = new CondCorrespondent();

		private IdeaList list = new IdeaList();

		@Theory public void interestize(String interestedParty, String idea) {
			list.add(idea);

			correspondent.alwaysChooseAnswerThat(is("true"));
			List<String> output = list.interestize(interestedParty,
					correspondent);
			assertThat(output, hasItem(containsStrings(interestedParty, idea,
					"true")));
		}

		@Theory public void includeIdeaNamesWhenInterestizing(String ideaName,
				String partyName) {
			list.add(ideaName);

			correspondent.alwaysChooseAnswerThat(is("true"));
			Capturer<CondCorrespondent> capturer = Capturer
					.forObject(correspondent);
			list.interestize(partyName, capturer.getProxy());
			assertThat(capturer.log(), containsStrings(ideaName, partyName));
		}

		@Theory public void includeIdeaNamesInBacklog(String idea1, String idea2) {
			assumeThat(idea1, not(idea2));

			correspondent.alwaysChooseAnswerThat(containsString("1"));

			list.add(idea1);
			list.add(idea2);
			Capturer<CondCorrespondent> capturer = Capturer
					.forObject(correspondent);
			list.backlog(capturer.getProxy(), 0.5);
			assertThat(capturer.log(), containsStrings(idea1, idea2));
		}

		@Test public void returnMultipleLinesFromBackLog() {
			list.add("Radiohead");
			list.add("Portishead");

			correspondent.alwaysChooseAnswerThat(containsString("Radiohead"));

			List<String> log = list.backlog(correspondent, 1.0);
			assertThat(log, is(asList("#before Radiohead Portishead",
					"0.5 Radiohead", "0.5 Portishead")));
		}

		@Test public void rememberSortingInBacklog() {
			list.add("Radiohead");
			list.add("Portishead");
			list.add("Eraserhead");

			correspondent.alwaysChooseAnswerThat(containsString("Eraserhead"));
			correspondent.alwaysChooseAnswerThat(containsString("Portishead"));

			List<String> log = list.backlog(correspondent, 1.0);
			assertThat(log, is(asList("#before Portishead Radiohead",
					"#before Eraserhead Portishead", "0.5 Eraserhead",
					"0.5 Portishead")));
		}

		@Test public void askForOrder() {
			list.add("Qui Gon");
			list.add("Obi Wan");

			correspondent.alwaysChooseAnswerThat(containsString("Obi"));

			assertThat(list.backlog(correspondent, 0.5),
					hasItem("#before [Obi Wan] [Qui Gon]"));
		}

		@Test(expected = None.class) public void silentlyAcceptNoInput() {
			list.backlog(correspondent, 0.5);
		}

		@Test public void outputBeforesLearnedDuringBackLog() {
			list.addWithEstimate("Qui Gon", 1.0);
			list.addWithEstimate("Obi Wan", 1.0);

			correspondent.alwaysChooseAnswerThat(containsString("Qui"));

			assertThat(list.backlog(correspondent, 1.0),
					hasItem("#before [Qui Gon] [Obi Wan]"));
		}

		@Theory public void avoidAskingTwiceForInterest(BracketedString idea,
				String partyName, boolean interest) {
			list.add(idea.toString());
			list.get(idea).interest(partyName, interest);

			// Will throw exception if question asked
			list.interestize(partyName, correspondent);
		}

		@Theory public void useDisplayedNameInDoneOutput(String idea,
				String partyName) {
			list.add(idea);

			correspondent.alwaysChooseAnswerThat(containsString("d)"));

			List<String> output = list.interestize(partyName, correspondent);
			assertThat(output, hasItem(containsString(idea)));
		}

		@Test public void askQuestionsInBacklog() {
			correspondent.alwaysChooseAnswerThat(containsString("Lunch"));
			list.add("Dinner");
			list.add("Lunch");
			assertThat(list.backlog(correspondent, 0.5), hasItem("0.5 Lunch"));
		}

		@Test public void asksGoodQuestionDuringEstimatization() {
			correspondent.alwaysChooseAnswerThat(containsString("correct"));
			list.add("Pica");
			List<String> output = list.estimatize(correspondent);
			assertThat(output, hasItem("#estimate Pica 0.5"));
		}
		
		@Test public void raiseEstimatesInQuestions() {
			adjustUntilCorrect("1.0", "more");
			list.add("Pica");
			List<String> output = list.estimatize(correspondent);
			assertThat(output, hasItem("#estimate Pica 1.0"));
		}

		@Test public void reduceEstimateInQuestion() {
			adjustUntilCorrect("0.0", "less");
			list.add("Pica");
			List<String> output = list.estimatize(correspondent);
			assertThat(output, hasItem("#estimate Pica 0.0"));
		}

		private void adjustUntilCorrect(String correctEstimate, String direction) {
			correspondent.ifQuestion(
					hasPromptThat(containsString(correctEstimate)))
					.thenChooseAnswerContaining(Idea.CORRECT_ESTIMATE);
			correspondent.alwaysChooseAnswerThat(containsString(direction));
		}
	}
}
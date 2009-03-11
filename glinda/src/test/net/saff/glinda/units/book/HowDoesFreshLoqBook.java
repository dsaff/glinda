/**
 * 
 */
package test.net.saff.glinda.units.book;

import static com.domainlanguage.time.Duration.days;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.EnumSet;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.ideas.IdeaList;
import net.saff.glinda.ideas.correspondent.CondCorrespondent;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementType;
import net.saff.glinda.projects.routines.RelativeTimeRoutineSpecification;
import net.saff.glinda.projects.routines.RoutineSpecificationParser;
import net.saff.glinda.projects.tasks.ActivityRequestor;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.stubbedtheories.Stub;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.Matchers;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesFreshLoqBook extends LoqBookDataPoints {
	private LoqBook book = LoqBook.withDefaults();

	@Theory
	public void listProjectsInStatus(BracketedString projectName,
			EnumSet<Flag> flags) {
		book.startProject(projectName);
		assertThat(book.status(flags), Matchers.hasLineContaining(projectName));
	}

	@Theory
	public void displayPausedProjects(BracketedString projectName) {
		book.startProject(projectName);
		book.getConcerns().findConcern(projectName).pause();
		assertThat(book.status(null), Matchers.hasLineContaining(projectName
				.toString()));
	}

	@Theory
	public void rememberIdeas(String idea) {
		book.idea(idea);
		assertThat(book.ideas(), Matchers.hasLineContaining(idea));
	}

	@Theory
	public void askQuestionsDuringInterestization(
			@Stub Correspondent correspondent) {
		assumeThat(correspondent.getAnswer("Is the king interested in MyIdea?",
				IdeaList.INTEREST_ANSWERS), is("true"));

		book.idea("MyIdea");
		book.setCorrespondent(correspondent);
		assertThat(book.interestize("the king"), Matchers
				.hasLineContaining("true"));
	}

	@Theory
	public void showGoals(BracketedString goalName) {
		book.getConcerns().startGoal(goalName);
		assertThat(book.goals().toString(), containsString(goalName.toString()));
	}

	@Theory
	public void calculateLengthOfPriorityOverride(BracketedString project1,
			BracketedString project2, GlindaTimePoint target) {
		assumeThat(project1, not(project2));
		GlindaTimePoint yesterday = target.minus(days(1));

		book.now(yesterday);
		book.startProject(project1);
		book.priorTo(book.getConcerns().startIfNeeded(project1), book
				.getConcerns().startIfNeeded(project2), null);
		book.setNow(yesterday);

		assertThat(book.status(Flag.NONE), Matchers.hasLineContaining(target
				.displayRelativeTo(yesterday)));
	}

	@Test
	public void calculateLengthOfPriorityOverrideOnBstringsAndJan1() {
		BracketedString project1 = bstrings[0];
		BracketedString project2 = bstrings[1];

		book.now(jan1);
		Project p1 = book.getConcerns().startIfNeeded(project1);
		Project p2 = book.getConcerns().startIfNeeded(project2);
		Requirement<Project> link = new Requirement<Project>(p1,
				RequirementType.SOFT, p2, book.linkDuration(null));
		book.getGraph().addReplacing(p1, link);
		book.setNow(jan1);

		StatusRequest context = book.context(Flag.NONE);
		assertFalse(context.getGraph().getChildren(p1).isEmpty());
	}

	@Theory
	public void rememberRoutines(BracketedString routine, int amHour)
			throws LoqCommandExecutionException {
		assumeTrue(0 < amHour);
		assumeTrue(amHour <= 12);
		book.getConcerns().startRoutine(
				new RelativeTimeRoutineSpecification(routine, amHour + "am"));
		assertThat(book.status(Flag.NONE), both(
				Matchers.hasLineContaining(routine)).and(
				Matchers.hasLineContaining("" + amHour)));
	}

	@Theory
	public void heapifyTwoTasks(BracketedString project1,
			BracketedString project2) {
		assumeThat(project1, not(project2));
		book.startProject(project1);
		book.startProject(project2);

		CondCorrespondent c = new CondCorrespondent();
		c.alwaysChooseAnswerThat(containsString(project1.deCamelCase()));
		book.setCorrespondent(c);

		assertThat(book.heapify(), Matchers.hasLineContaining(project2 + " "
				+ project1));
	}

	@Theory
	public void distinguishWhenAProjectDependsOnARoutine(
			BracketedString activeProject, BracketedString inactiveProject,
			BracketedString routine, GlindaTimePoint now)
			throws LoqCommandExecutionException {
		assumeThat(activeProject, not(inactiveProject));
		assumeThat(activeProject, not(routine));
		assumeThat(inactiveProject, not(routine));
		book.startProject(activeProject);
		book.startProject(inactiveProject);
		book.getConcerns().startRoutine(
				new RoutineSpecificationParser().parse2(routine,
						"each day at 3pm", GlindaTimePoint.now()));
		book.now(now);
		book.nextStep(activeProject, routine);
		book.done(routine);

		GlindaTimePoint tomorrow = now.plus(days(1));
		book.now(tomorrow);
		book.nextStep(inactiveProject, routine);

		StatusRequest request = StatusRequest.asOf(tomorrow, book)
				.pretendingFor(null).withFlags(Flag.NONE).makeRequest();
		assertTrue(new ActivityRequestor(request).isActionable(book
				.getConcerns().findConcern(activeProject)));
	}
}

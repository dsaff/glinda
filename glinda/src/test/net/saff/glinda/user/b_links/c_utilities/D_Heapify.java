/**
 * 
 */
package test.net.saff.glinda.user.b_links.c_utilities;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.containsString;
import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.correspondent.UserKeyboardCorrespondent;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.tasks.Task;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.HumanUser;
import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;
import test.net.saff.glinda.testclasstypes.OnDiskLoqBookTest;

public class D_Heapify extends CondCorrespondentOnDiskTest {
	@Test
	public void createAHeap() {
		r("#startProject AAA");
		r("#startProject BBB");
		r("#startProject CCC");
		r("#startProject DDD");

		alwaysAnswer("A a a.");
		otherwiseChooseAnswerContaining("B b b.");
		otherwiseChooseAnswerContaining("C c c.");

		run("heapify");
		w("heapify:");
		w("#priorTo BBB AAA");
		w("#priorTo CCC AAA");
		w("#priorTo DDD BBB");
	}

	@RunWith(Theories.class)
	public static class HeapifyComplainsOnNullCorrespondentReturn extends
			LoqBookDataPoints {
		@Theory
		public void heapifyComplainsOnNullReturn(BracketedString project1,
				BracketedString project2, GlindaTimePoint now) {
			assumeThat(project1, not(project2));
			GoingConcerns concerns = GoingConcerns.withDefaults();
			concerns.startProject(project1);
			concerns.startProject(project2);
			try {
				new StatusRequestor(StatusRequest.asOf(now,
						new RequirementGraph<Project>()).pretendingFor(null)
						.withFlags(Flag.NONE).makeRequest()).heapify(
						new Correspondent() {
							public String getAnswer(String question,
									String... bucket) {
								return null;
							}
						}, concerns.concerns().values());
				fail("Should have thrown exception");
			} catch (Exception e) {
				assertThat(e.getMessage(), containsString(project1
						.deCamelCase()));
			}
		}

		@Test
		public void deCamelCaseCars() {
			assertThat(new BracketedString("Cars").deCamelCase(), is("Cars"));
		}
	}

	@Test
	public void ignoreInactiveTasks() {
		addImport(Project.class, "pause");
		r("#startProject AAA");
		r("#startProject BBB");
		r("#startProject CCC");
		r("#startProject DDD");
		r("#pause DDD");

		alwaysAnswer("A a a");
		otherwiseChooseAnswerContaining("B b b");
		otherwiseChooseAnswerContaining("C c c");

		run("heapify");
		w("heapify:");
		w("#priorTo BBB AAA");
		w("#priorTo CCC AAA");
		done();
	}

	@Test
	public void provideNumbersForKeyboardFastness() {
		addImport(Project.class, "pause");
		r("#startProject AAA");
		r("#startProject BBB");

		alwaysAnswer("2) A a a");

		run("heapify");
		w("heapify:");
		w("#priorTo BBB AAA");
		done();
	}

	@Test
	public void breakUpCamelCase() {
		addImport(Project.class, "pause");
		r("#startProject DoGoodThings");
		r("#startProject DoGreatThings");

		alwaysAnswer("2) Do good things.");

		run("heapify");
		w("heapify:");
		w("#priorTo DoGreatThings DoGoodThings");
		done();
	}

	public static class HumanUserDoesHeapify extends OnDiskLoqBookTest {
		@Test
		public void breakUpCamelCaseWithHumanUser() {
			assumeTrue(HumanUser.isAvailable());
			r("#startProject ChooseThisOne");
			r("#startProject NotThisOne");

			run("heapify");
			w("heapify:");
			w("#priorTo NotThisOne ChooseThisOne");
			done();
		}

		@Test
		public void breakUpCamelCaseWithHumanUserTheOtherWay() {
			assumeTrue(HumanUser.isAvailable());
			r("#startProject NotThisOne");
			r("#startProject ChooseThisOne");

			run("heapify");
			w("heapify:");
			w("#priorTo NotThisOne ChooseThisOne");
			done();
		}

		@Override
		protected Correspondent getCorrespondent() {
			return new UserKeyboardCorrespondent();
		}
	}

	@RunWith(Theories.class)
	public static class ProjectSupportsDeCamelCasingInHeapify extends
			LoqBookDataPoints {
		@Theory
		public void test(String a, String b) {
			String camelCase = initCaps(a) + initCaps(b);
			assertThat(Task.withoutListener(camelCase).heapifyChoice(1),
					containsString(lastChar(a) + " "
							+ firstChar(b.toLowerCase())));
		}

		private String firstChar(String string) {
			return string.substring(0, 1);
		}

		private String lastChar(String string) {
			return string.substring(string.length() - 1);
		}

		private String initCaps(String string) {
			return firstChar(string).toUpperCase()
					+ string.substring(1).toLowerCase();
		}
	}

	@Test(expected = Throwable.class)
	public void gripeIfCommandNotFound() {
		r("#notACommand");
		run("heapify");
	}
}
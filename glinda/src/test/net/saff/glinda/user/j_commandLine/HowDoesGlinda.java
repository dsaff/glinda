package test.net.saff.glinda.user.j_commandLine;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.core.GlindaInvocation;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.JournalFile;
import net.saff.glinda.journal.JournalParser;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.time.GlindaTimePointParser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import test.net.saff.glinda.testclasstypes.TempDirTest;

public class HowDoesGlinda extends TempDirTest {
	public static class ParallelRunner extends BlockJUnit4ClassRunner {
		private ExecutorService fService = Executors.newCachedThreadPool();
		private List<Future<Object>> fResults = new ArrayList<Future<Object>>();

		public ParallelRunner(Class<?> klass) throws InitializationError {
			super(klass);
		}

		@Override
		protected void runChild(final FrameworkMethod method,
				final RunNotifier notifier) {
			Callable<Object> callable = new Callable<Object>() {
				public Object call() throws Exception {
					superRunChild(method, notifier);
					return null;
				}
			};
			fResults.add(fService.submit(callable));
		}

		protected void superRunChild(FrameworkMethod method,
				RunNotifier notifier) {
			super.runChild(method, notifier);
		}

		@Override
		public void run(RunNotifier notifier) {
			super.run(notifier);
			for (Future<Object> each : fResults)
				try {
					each.get(2000, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	@Test(timeout = 4000)
	public void determineStatusQuickly() throws Exception {
		runStatus("31kjournal.txt");
	}

	@Test(timeout = 4000)
	public void determineStatusQuicklyOnAnotherJournal() throws Exception {
		runStatus("31kjournal_long.txt");
	}

	// TODO: reinstate
	@Ignore
	@Test
	public void showMovingCarsWhenAppropriate() throws Exception {
		assertThat(runStatus("shouldMoveCars.txt"),
				containsString("! p:MoveCarsForThursday"));
	}

	// TODO: DUP above
	@Test
	public void neverReturnedNull() throws Exception {
		GlindaInvocation invocation = invocation("AllNull.txt");
		JournalParser<LoqBook> r = invocation.readParser();
		LoqBook r2 = r.getTarget().getTarget();
		StatusRequestor r3 = new StatusRequestor(r2.context(EnumSet.of(
				Flag.withParents, Flag.withTimes, Flag.actionItemsFirst)));
		for (Project project : r2.getConcerns().concerns().values()) {
			Status<Project> status = r3.status(project);
			String statusLine = r3.getStatusRequest().format(
					r3.nameForStatusLine(project), status);
			assertThat(statusLine, not(containsString("returned null")));
		}
	}

	// TODO: reinstate
	@Ignore
	@Test
	public void doNotShowExpiredSoftLinksProject() throws Exception {
		new JournalFile(resourceFile("127kjournal.txt"), false) {
			@Override
			protected JournalParser<LoqBook> resetParser() {
				return new JournalParser<LoqBook>(isIgnoreProblems()) {
					@Override
					public void a(String line) {
						if (line.startsWith("#NOW")) {
							getTarget().getTarget().now(
									new GlindaTimePointParser().parse(line
											.substring(5)));
							return;
						}
						try {
							super.a(line);
							if (line
									.startsWith("#edit ShowIgnoredSoftLink ShowExpiredSoftLink"))
								fail("Should have thrown an exception when trying to edit one task to another's name");
						} catch (LoqCommandExecutionException e) {
							// do nothing
						}
					}
				};
			}
		}.readParser(GoingConcerns.withDefaults());
	}

	private String runStatus(String journalFileName) throws Exception {
		return invocation(journalFileName).output();
	}

	private static GlindaInvocation invocation(String journalFileName) {
		return GlindaInvocation.fromLocation(resourceFile(journalFileName),
				"status", "-withTimes", "-actionItemsFirst", "-withParents");
	}

	private static String resourceFile(String journalFileName) {
		return "src/test/resources/" + journalFileName;
	}
}

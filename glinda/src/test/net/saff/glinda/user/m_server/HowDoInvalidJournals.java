package test.net.saff.glinda.user.m_server;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.saff.glinda.core.GlindaInvocation;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class HowDoInvalidJournals extends ServerTest {
	@Theory
	public void returnStackTraceFromException(String projectName)
			throws IOException {
		String fileName = exampleProject(projectName).writeInvalidJournal();
		assertThat(statusFromServer(fileName), containsString(projectName));
	}

	@Test
	public void clientDoesNotReadJournalFile() throws IOException {
		String fileName = exampleProject("Whatever").writeInvalidJournal();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(output);
		GlindaInvocation.fromLocation(fileName, "client", "4444", "-noRetries", "status")
				.printOutput(printStream);
		assertThat(output.toString(), containsString("Cannot connect"));
		assertThat(output.toString(), not(containsString("Whatever")));
	}
}

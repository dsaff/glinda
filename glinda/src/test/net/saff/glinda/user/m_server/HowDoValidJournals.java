package test.net.saff.glinda.user.m_server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.UnknownHostException;

import javax.net.ServerSocketFactory;

import net.saff.glinda.core.GlindaInvocation;
import net.saff.glinda.core.Port;
import net.saff.glinda.core.Server;
import net.saff.glinda.core.Port.NetworkPort;
import net.saff.glinda.journal.FreedomFile;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.Dupple;

@RunWith(Theories.class)
public class HowDoValidJournals extends ServerTest {
	@Test
	public void serveStatus() throws UnknownHostException, IOException {
		String fileName = exampleProject("BeccaTakes").writeValidJournal();
		String output = statusFromServer(fileName);
		assertThat(output, containsString("BeccaTakes"));
	}

	@Test
	public void readCommand() throws UnknownHostException, IOException {
		String fileName = exampleProject("BeccaTakes").writeValidJournal();
		String command = "goalStatus BeccaTakes";
		String output = runOnOneShotServer(fileName, command);
		assertThat(output, containsString("goalStatus"));
	}

	@Test
	public void serveStatusRepeatedly() throws UnknownHostException,
			IOException {
		String fileName = exampleProject("BeccaTakes").writeValidJournal();
		Server server = getPort().createServer(fileName);
		String output = server.output("status");
		String output2 = server.output("status");
		assertThat(output, containsString("BeccaTakes"));
		assertThat(output2, containsString("BeccaTakes"));
	}

	@Test
	public void noticeChanges() throws IOException {
		String fileName = exampleProject("BeccaTakes").writeValidJournal();
		Server server = getPort().createServer(fileName);
		server.output("status");

		FileWriter writer = new FileWriter(fileName, true);
		writer.append("#startProject JumpAndJump");
		writer.close();

		String output2 = server.output("status");
		assertThat(output2, containsString("JumpAndJump"));
	}

	@Test
	public void workThroughCommandLine() throws IOException {
		final String fileName = exampleProject("BeccaTakes")
				.writeValidJournal();
		startCommandLineServer(fileName);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		runCommandLineClient("status", output);
		runCommandLineClient("stopServer", output);
		assertThat(output.toString(), containsString("BeccaTakes"));
	}

	private void startCommandLineServer(final String fileName) {
		new Thread(new Runnable() {
			public void run() {
				PrintStream printStream = new PrintStream(
						new ByteArrayOutputStream());
				GlindaInvocation glindaInvocation = GlindaInvocation
						.fromLocation(fileName, "server", portNumberString());
				glindaInvocation.printOutput(printStream);
			}
		}).start();
	}

	private void runCommandLineClient(String command,
			ByteArrayOutputStream output) {
		new GlindaInvocation(FreedomFile.NULL, new IterateSupersmashStrategy(),
				"client", portNumberString(), command)
				.printOutput(new PrintStream(output));
	}

	private String portNumberString() {
		return "4444";
	}

	@Test
	public void readJournalFile(String projectName) throws IOException {
		String fileName = exampleProject(projectName).writeValidJournal();
		assertThat(statusFromServer(fileName), containsString(projectName));
	}

	@Theory
	public void allowDifferentPorts(int portNumber)
			throws UnknownHostException, IOException {
		ServerSocketFactory factory = Dupple
				.recorder(ServerSocketFactory.class);
		NetworkPort port = (NetworkPort) Port.fromPortNumber(factory,
				portNumber);
		port.serverSocket();
		Dupple.assertCalled(factory).createServerSocket(portNumber);
	}

	@Test
	public void preserveNewlines() throws IOException {
		String textWithNewLines = "abc\ndef\nghi\n";
		assertThat(getPort().readUntilDelimiter(
				new BufferedReader(new StringReader(textWithNewLines)),
				"NOT IN STRING"), is(textWithNewLines));
	}
}

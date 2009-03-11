package test.net.saff.glinda.user.m_server;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import test.net.saff.glinda.HumanUser;

@RunWith(Theories.class) public class HowDoUserInputCommands extends ServerTest {
	@Test public void askForUserInput() throws IOException {
		assumeTrue(HumanUser.isAvailable());
		String fileName = writeJournalWithTwoProjects();
		String output = runOnOneShotServer(fileName, "heapify");
		assertThat(output, not(containsString("Exception")));
	}

	private String writeJournalWithTwoProjects() throws IOException {
		String fileName = TEMP_DIR + "/j.txt";
		FileWriter writer = new FileWriter(new File(fileName));
		writer.append("#startProject DoFiveThings\n");
		writer.append("#startProject DoSixThings\n");
		writer.close();
		return fileName;
	}
}

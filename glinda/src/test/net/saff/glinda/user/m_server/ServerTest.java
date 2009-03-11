package test.net.saff.glinda.user.m_server;

import net.saff.glinda.core.ExampleProjectName;
import net.saff.glinda.core.Port;

import org.junit.experimental.theories.DataPoints;

import test.net.saff.glinda.testclasstypes.TempDirTest;

public class ServerTest extends TempDirTest {
	@DataPoints
	public static String[] strings = { "George", "Abraham" };
	@DataPoints
	public static int[] ints = { 4444, 8888 };

	private final Port port = Port.local();

	protected String runOnOneShotServer(String fileName, String command) {
		return getPort().createServer(fileName).output(command);
	}

	protected ExampleProjectName exampleProject(String projectName) {
		return new ExampleProjectName(projectName, TEMP_DIR);
	}

	protected String statusFromServer(String fileName) {
		return runOnOneShotServer(fileName, "status");
	}

	public Port getPort() {
		return port;
	}

}

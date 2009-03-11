package net.saff.glinda.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ExampleProjectName {
	private String projectName;
	private File tempDir;

	public ExampleProjectName(String projectName, File tempDir) {
		this.projectName = projectName;
		this.tempDir = tempDir;
	}

	public String writeInvalidJournal() throws IOException {
		return writeJournal(false);
	}

	public String writeValidJournal() throws IOException {
		return writeJournal(true);
	}

	private String writeJournal(boolean appendTime) throws IOException {
		String fileName = tempDir + "/j.txt";
		FileWriter writer = new FileWriter(new File(fileName));
		if (appendTime)
			writer.append("#NOW 2007-01-01 12:00:00\n");
		writer.append("#track " + projectName + " 0\n");
		writer.close();
		return fileName;
	}

	public Server startServer(Port port) throws IOException {
		return port.startServerInNewThread(writeValidJournal());
	}
}
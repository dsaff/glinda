package net.saff.glinda.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;

import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.ideas.correspondent.UserKeyboardCorrespondent;
import net.saff.glinda.interpretation.finding.CommandStrings;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.JournalFile;
import net.saff.glinda.time.GlindaTimePoint;

public class Server {
	private boolean stopped = false;
	private final SeaOtterServerSocket serverSocket;
	private JournalFile journalFile;

	public Server(JournalFile journal, SeaOtterServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		this.journalFile = journal;
	}

	public void runInNewThread() {
		new Thread(new Runnable() {
			public void run() {
				Server.this.run();
			}
		}).start();
	}

	// TODO (Jun 16, 2008 6:55:20 PM): privatize?
	public void run() {
		try {
			while (!stopped) {
				try {
					SeaOtterClientSocket clientSocket = serverSocket.accept();
					try {
						writeResult(clientSocket, readCommand(clientSocket));
					} finally {
						clientSocket.close();
					}
				} catch (SocketException e) {
					// assuming just socket closed
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeResult(SeaOtterClientSocket clientSocket, String[] command)
			throws IOException {
		if (command[0].equals("stopServer"))
			stop();
		clientSocket.getOutputStream().write(
				(output(command) + "DONE!\n").getBytes());
	}

	public String output(String... commands) {
		try {
			return journalFile.readParser(GoingConcerns.withDefaults()).invoke(
					GlindaTimePoint.now(), new UserKeyboardCorrespondent(),
					new CommandStrings(commands));
		} catch (LoqCommandExecutionException e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	private String[] readCommand(SeaOtterClientSocket clientSocket)
			throws IOException {
		return new BufferedReader(new InputStreamReader(clientSocket
				.getInputStream())).readLine().split(" ");
	}

	public void stop() throws IOException {
		stopped = true;
		serverSocket.close();
	}
}

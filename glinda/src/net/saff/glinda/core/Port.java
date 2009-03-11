package net.saff.glinda.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;

import javax.net.ServerSocketFactory;

import net.saff.glinda.interpretation.finding.CommandStrings;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.JournalFile;

public abstract class Port {
	private static class LocalPort extends Port {
		private final Pipe serverToClient;
		private final Pipe clientToServer;

		private LocalPort() {
			clientToServer = openPipe();
			serverToClient = openPipe();
		}

		private Pipe openPipe() {
			try {
				return Pipe.open();
			} catch (IOException e) {
				throw new RuntimeException("didn't ever expect this", e);
			}
		}

		@Override
		public SeaOtterServerSocket serverSocket() {
			return new SeaOtterServerSocket() {
				@Override
				public void close() {
				}

				@Override
				public SeaOtterClientSocket accept() {
					return new SeaOtterClientSocket() {
						@Override
						public OutputStream getOutputStream() {
							return Channels.newOutputStream(serverToClient
									.sink());
						}

						@Override
						public InputStream getInputStream() {
							return Channels.newInputStream(clientToServer
									.source());
						}

						@Override
						public void close() {
						}
					};
				}
			};
		}

		@Override
		public SeaOtterClientSocket clientSocket(int retries) {
			return new SeaOtterClientSocket() {

				@Override
				public OutputStream getOutputStream() {
					return Channels.newOutputStream(clientToServer.sink());
				}

				@Override
				public InputStream getInputStream() {
					return Channels.newInputStream(serverToClient.source());
				}

				@Override
				public void close() {
				}
			};
		}
	}

	public static class NetworkPort extends Port {
		private final int portNumber;
		private final ServerSocketFactory socketFactory;

		private NetworkPort(ServerSocketFactory factory, int portNumber) {
			this.socketFactory = factory;
			this.portNumber = portNumber;
		}

		@Override
		public String toString() {
			return "" + portNumber;
		}

		@Override
		public SeaOtterServerSocket serverSocket() throws IOException {
			return SeaOtterServerSocket.fromServerSocket(socketFactory
					.createServerSocket(portNumber));
		}

		public SeaOtterClientSocket clientSocket(int retries)
				throws LoqCommandExecutionException {
			int retried = 0;
			while (retried++ < retries) {
				try {
					return SeaOtterClientSocket.fromNetworkSocket(new Socket(
							(String) null, portNumber));
				} catch (Exception e) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// do nothing and try again
					}
					// do it again
				}
			}
			throw new LoqCommandExecutionException("Cannot connect to server");
		}
	}

	// TODO (Jun 16, 2008 7:11:32 PM): pass-through
	public static Port fromPortNumber(ServerSocketFactory factory,
			int portNumber) {
		return new NetworkPort(factory, portNumber);
	}

	public static Port local() {
		return new LocalPort();
	}

	public Server startServerInNewThread(String fileName) {
		Server server = createServer(fileName);
		server.runInNewThread();
		return server;
	}

	public String runClient(CommandStrings commandStrings)
			throws LoqCommandExecutionException {
		try {
			return runClientUnsafely(commandStrings);
		} catch (IOException e) {
			throw new LoqCommandExecutionException(e);
		}
	}

	public String runClientUnsafely(CommandStrings commandStrings)
			throws IOException, LoqCommandExecutionException {
		if (commandStrings.getFirst().equals("-noRetries"))
			return commandOutput(0, commandStrings.getRest());
		return commandOutput(10, commandStrings);
	}

	public String commandOutput(int retries, CommandStrings commandStrings)
			throws IOException, LoqCommandExecutionException {
		return commandOutput(clientSocket(retries), commandStrings.join());
	}

	public String commandOutput(SeaOtterClientSocket socket, String command)
			throws IOException {
		socket.getOutputStream().write((command + "\n").getBytes());
		return readUntilDelimiter(socket, "DONE!");
	}

	public String readUntilDelimiter(SeaOtterClientSocket socket,
			String delimiter) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		return readUntilDelimiter(reader, delimiter);
	}

	public String readUntilDelimiter(BufferedReader reader, String delimiter)
			throws IOException {
		String output = "";
		while (true) {
			String line = reader.readLine();
			if (line == null || line.equals(delimiter))
				return output;
			output += line + "\n";
		}
	}

	public abstract SeaOtterClientSocket clientSocket(int retries)
			throws LoqCommandExecutionException;

	public Server createServer(JournalFile journal) {
		try {
			return createSocketedServer(journal);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Server createSocketedServer(JournalFile journal) throws IOException {
		return new Server(journal, serverSocket());
	}

	public abstract SeaOtterServerSocket serverSocket() throws IOException;

	public String runStatus() throws LoqCommandExecutionException {
		return runClient(new CommandStrings("status"));
	}

	public Server createServer(String fileName) {
		return createServer(new JournalFile(fileName, true));
	}
}

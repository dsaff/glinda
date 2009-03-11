package net.saff.glinda.core;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class SeaOtterServerSocket {
	public static SeaOtterServerSocket fromServerSocket(
			final ServerSocket serverSocket) {
		return new SeaOtterServerSocket() {
			@Override
			public void close() throws IOException {
				serverSocket.close();
			}

			@Override
			public SeaOtterClientSocket accept() throws IOException {
				return SeaOtterClientSocket.fromNetworkSocket(serverSocket
						.accept());
			}
		};
	}

	public abstract SeaOtterClientSocket accept() throws IOException;

	public abstract void close() throws IOException;
}

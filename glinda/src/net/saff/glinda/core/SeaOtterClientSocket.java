package net.saff.glinda.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class SeaOtterClientSocket {
	public abstract InputStream getInputStream() throws IOException;

	public abstract void close() throws IOException;

	public abstract OutputStream getOutputStream() throws IOException;

	public static SeaOtterClientSocket fromNetworkSocket(final Socket socket) {
		return new SeaOtterClientSocket() {
			@Override
			public void close() throws IOException {
				socket.close();
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return socket.getInputStream();
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				return socket.getOutputStream();
			}
		};
	}
}

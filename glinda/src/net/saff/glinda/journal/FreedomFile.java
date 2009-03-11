package net.saff.glinda.journal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public abstract class FreedomFile {

	public static final FreedomFile NULL = null;

	public static FreedomFile fromString(final String string) {
		return new FreedomFile() {
			@Override
			public BufferedReader createFreshReader() {
				return new BufferedReader(new StringReader(string));
			}

			@Override
			public BufferedReader createReentrantReader() {
				return null;
			}
		};
	}

	public abstract BufferedReader createReentrantReader() throws IOException;

	public abstract BufferedReader createFreshReader() throws IOException;

	public static FreedomFile fromFile(final String fileName) {
		return new FreedomFile() {
			long lastPosition = 0;
			byte[] lastFewBytes = null;

			@Override
			public BufferedReader createFreshReader() throws IOException {
				lastPosition = 0;
				RandomAccessFile file = file(fileName);
				FileChannel channel = file.getChannel();
				return reader(file, channel);
			}

			@Override
			public BufferedReader createReentrantReader() throws IOException {
				if (lastFewBytes == null)
					return null;
				RandomAccessFile file = file(fileName);
				FileChannel channel = file.getChannel();

				if (!Arrays.equals(lastFewBytes, lastFewBytes(channel)))
					return close(channel);

				return reader(file, channel);
			}

			private RandomAccessFile file(final String fileName)
					throws FileNotFoundException {
				return new RandomAccessFile(fileName, "r");
			}

			private BufferedReader reader(RandomAccessFile file,
					final FileChannel channel) throws IOException {
				channel.position(lastPosition);
				lastPosition = file.length();

				return new BufferedReader(Channels.newReader(channel,
						"ISO-8859-1")) {
					@Override
					public void close() throws IOException {
						lastFewBytes = lastFewBytes(channel);
						super.close();
					}
				};
			}

			private BufferedReader close(FileChannel channel)
					throws IOException {
				channel.close();
				return null;
			}

			private byte[] lastFewBytes(FileChannel channel) throws IOException {
				long beforeLastFew = Math.max(0, lastPosition - 100);
				ByteBuffer buffer = ByteBuffer
						.allocate((int) (lastPosition - beforeLastFew));
				channel.read(buffer, beforeLastFew);
				return buffer.array();
			}
		};
	}
}

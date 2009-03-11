package net.saff.experiments;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

public class FindFastestCharset {
	public static void main(String[] args) {
		SortedMap<String, Charset> charsets = Charset.availableCharsets();
		Set<Entry<String, Charset>> entries = charsets.entrySet();
		for (Entry<String, Charset> each : entries) {
			try {
				Charset charset = each.getValue();
				System.out.println(each.getKey() + ": "
						+ timeStream(standardInputStream(), charset));
				System.out.println(each.getKey() + " (channel): "
						+ timeStream(channelStream(), charset));
				System.out.println(each.getKey() + " (map channel): "
						+ timeWithFileChannel(charset));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static InputStream channelStream() throws FileNotFoundException {
		return Channels.newInputStream(channel());
	}

	private static FileChannel channel() throws FileNotFoundException {
		return new RandomAccessFile("src/test/resources/64kjournal.txt", "r")
				.getChannel();
	}

	private static FileInputStream standardInputStream()
			throws FileNotFoundException {
		return new FileInputStream("src/test/resources/64kjournal.txt");
	}

	private static long timeStream(InputStream inputStream, Charset charset)
			throws IOException {
		return time(new BufferedReader(new InputStreamReader(inputStream,
				charset)));
	}

	private static long timeWithFileChannel(Charset charset) throws IOException {
		return time(new BufferedReader(Channels.newReader(channel(), charset
				.newDecoder(), -1)));
	}
	private static long time(BufferedReader reader) throws IOException {
		long start = System.currentTimeMillis();
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
		}
		long end = System.currentTimeMillis();
		return end - start;
	}
}

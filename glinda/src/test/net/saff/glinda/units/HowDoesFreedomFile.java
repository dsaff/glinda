package test.net.saff.glinda.units;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import net.saff.glinda.journal.FreedomFile;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.TempDirTest;

public class HowDoesFreedomFile extends TempDirTest {
	@Test
	public void createFreshFile() throws IOException {
		assumeTrue(LotsOfTime.onOurHands());
		String fileName = TEMP_DIR + "/a.txt";
		write(fileName, "abc\n");
		FreedomFile file = FreedomFile.fromFile(fileName);
		getFirstLine(file);
		assertThat(getFirstLine(file), is("abc"));
	}

	@Test
	public void detectChanges() throws IOException {
		String fileName = TEMP_DIR + "/a.txt";
		write(fileName, "abc\n");
		FreedomFile file = FreedomFile.fromFile(fileName);
		getFirstLine(file);

		write(fileName, "def\n");

		assertThat(file.createReentrantReader(), nullValue());
	}

	private void write(String fileName, String string) throws IOException {
		FileWriter writer = new FileWriter(fileName);
		writer.append(string);
		writer.close();
	}

	private String getFirstLine(FreedomFile file) throws IOException {
		BufferedReader reader = file.createFreshReader();
		try {
			return reader.readLine();
		} finally {
			reader.close();
		}
	}
}

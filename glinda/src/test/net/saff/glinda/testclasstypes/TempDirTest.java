/**
 * 
 */
package test.net.saff.glinda.testclasstypes;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TempDirTest {
	protected static final File TEMP_DIR = new File("testTemp");

	@BeforeClass
	public static void createTempDir() {
		TEMP_DIR.mkdir();
	}

	@After
	public void removeTempDirFiles() {
		File[] files = TEMP_DIR.listFiles();
		if (files != null)
			for (File file : files)
				file.delete();
	}
	
	@AfterClass
	public static void removeTempDir() {
		TEMP_DIR.delete();
	}
}
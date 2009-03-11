/**
 * 
 */
package test.net.saff.glinda.testclasstypes;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class HowDoesTempDirTest extends TempDirTest {
	@Test
	public void deleteTempDirAfter() {
		removeTempDir();
		assertFalse(TEMP_DIR.exists());
	}
}
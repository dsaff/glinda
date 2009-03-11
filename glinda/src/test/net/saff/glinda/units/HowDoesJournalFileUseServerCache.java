package test.net.saff.glinda.units;

import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import net.saff.glinda.core.GlindaInvocation;
import net.saff.glinda.journal.JournalFile;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;

import org.junit.Before;
import org.junit.Test;

import test.net.saff.glinda.testclasstypes.TempDirTest;

public class HowDoesJournalFileUseServerCache extends TempDirTest {
  private JournalFile journal;

  @Before
  public void setUpServer() throws Exception {
    assumeTrue(LotsOfTime.onOurHands());
    copyFile(new File("src/test/resources/64kjournal.txt"), new File(
        journalName()));
    journal = new JournalFile(journalName(), true);
    status();
  }

  // @Test(timeout = 50)
  @Test
  public void test() throws Exception {
    FileWriter writer = new FileWriter(journalName(), true);
    writer.append("\n#startProject MoveAllZigs\n");
    writer.close();
    assertThat(status(), containsString("MoveAllZigs"));
    assertThat(status(), containsString("BeccaTakes"));
  }

  private String journalName() {
    return TEMP_DIR + "/journal.txt";
  }

  private String status() throws Exception {
    return new GlindaInvocation(journal, new IterateSupersmashStrategy(),
        "status").output();
  }

  private void copyFile(File toBeCopied, File newCopy) throws IOException {
    FileInputStream fis = new FileInputStream(toBeCopied);
    FileChannel fcin = fis.getChannel();
    FileOutputStream fos = new FileOutputStream(newCopy);
    FileChannel fcout = fos.getChannel();

    fcin.transferTo(0, fcin.size(), fcout);

    fcin.close();
    fcout.close();
    fis.close();
    fos.close();
  }
}

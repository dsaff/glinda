/**
 * 
 */
package test.net.saff.glinda.units.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.book.StaticParser;
import net.saff.glinda.interpretation.finding.CommandStrings;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.JournalParser;
import net.saff.glinda.journal.ObjectJournalParser;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class HowDoesLoqBookParser {
  @RunWith(Theories.class)
  public static class AtAllTimes extends LoqBookDataPoints {
    @DataPoints
    public static String[] strings = {"#fail badly", "#fail"};
    private JournalParser<Object> parser;

    public AtAllTimes(JournalParser<Object> g) {
      this.parser = g;
    }

    @Theory
    public void reportStringThatFailsParsing(String line) {
      try {
        parser.a(line);
        assumeTrue(false); // assume exception thrown
      } catch (LoqCommandExecutionException e) {
        assertThat(e.getMessage(), containsString(line));
      }
    }

    public static class BadSetNow {
      public void setNow(@SuppressWarnings("unused")
      GlindaTimePoint now) {
        throw new IllegalStateException();
      }
    }

    public static interface NotAClass {

    }

    @Theory
    public void reportErrorWhenSettingNow() {
      parser.setTarget(new BadSetNow());
      try {
        parser.nowIs(GlindaTimePoint.now());
        fail("Should have thrown exception");
      } catch (LoqCommandExecutionException e) {
        // success!
      }
    }

    @Theory
    public void reportErrorWhenClassCantBeInstantiated() {
      try {
        parser.a("##target " + NotAClass.class.getName());
        fail("Should have thrown exception");
      } catch (LoqCommandExecutionException e) {
        // success!
      }
    }

    @Theory
    public void reportErrorWhenImportDoesntExist() {
      try {
        parser.a("##import java.lang.String.notAMethodOnString");
        fail("Should have thrown exception");
      } catch (LoqCommandExecutionException e) {
        // success!
      }
    }

    @Theory
    public void includeImportsInOutputCommand()
        throws LoqCommandExecutionException {
      parser.a("##import java.lang.String.toUpperCase");
      assertThat(parser.output(new CommandStrings("toUpperCase", "abc")),
          is("toUpperCase: ABC\n"));
    }

    @Test
    public void findPotentialMethodWithFewestParameters()
        throws LoqCommandExecutionException, SecurityException,
        NoSuchMethodException {
      parser.a("##import java.lang.String.toUpperCase");
      assertEquals(GlindaMethod.forMethod(
          String.class.getMethod("toUpperCase"), new StaticParser()), parser
          .getImports().get(0));
    }
  }

  public static class WhenFresh {
    private ObjectJournalParser ui = new ObjectJournalParser(true);

    @Before
    public void setTarget() {
      ui.setTarget(LoqBook.withDefaults());
    }

    @Test(expected = LoqCommandExecutionException.class)
    public void wrapExceptionsAppropriately()
        throws LoqCommandExecutionException {
      output(new CommandStrings("thisIsNotAMethodName"));
    }

    @Test(expected = None.class)
    public void unknownMethodsAreFine() throws LoqCommandExecutionException {
      ignoreUnknownMethods();
    }

    @Test(expected = None.class)
    public void ignoreUnknownMethods() throws LoqCommandExecutionException {
      ui.a("#thisIsNotAMethodName 5");
    }

    @Test
    public void ignoreMethodCase() throws LoqCommandExecutionException {
      ui.a("#STARTGOAL Flowers");
      assertThat(output(new CommandStrings("goals")), is("goals:\nFlowers\n"));
    }

    @Test(expected = Throwable.class)
    public void throwExceptionWhenLineIsVeryWrong()
        throws LoqCommandExecutionException {
      ui.a("##import " + this.getClass() + ".fail");
      ui.a("#fail");
    }

    public static void fail() {
      throw new AssertionError();
    }

    @Test
    public void recognizeUselessCommands() {
      assertFalse(ui.isUseful("#thisReallyShouldntBeAMethodName"));
    }

    private String output(CommandStrings command)
        throws LoqCommandExecutionException {
      return ui.output(command);
    }
  }
}

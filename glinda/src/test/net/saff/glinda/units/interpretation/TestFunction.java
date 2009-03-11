package test.net.saff.glinda.units.interpretation;

import static java.util.Arrays.asList;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Method;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.interpretation.finding.Function;
import net.saff.glinda.interpretation.finding.UnspecifiedStringTarget;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class TestFunction extends LoqBookDataPoints {
  @Theory
  public void toStringHasImportantInfo(Object target, int methodIndex) {
    Method[] methods = target.getClass().getMethods();
    assumeTrue(methodIndex < methods.length);
    assertThat(Function.of(new UnspecifiedStringTarget(target), methods[0],
        null).toString(), containsStrings(target, methods[0].getName()));
  }

  @Test(expected = None.class)
  public void parseDates() throws Exception {
    LoqBook book = LoqBook.withDefaults();
    Function function =
        Function.of(new UnspecifiedStringTarget(book), LoqBook.class
            .getMethod("now", GlindaTimePoint.class), book);
    function.invoke(asList("2007-01-01 12:00:00"));
  }
}

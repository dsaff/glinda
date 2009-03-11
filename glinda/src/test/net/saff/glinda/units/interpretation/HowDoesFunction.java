package test.net.saff.glinda.units.interpretation;

import static java.util.Arrays.asList;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static net.saff.glinda.ideas.correspondent.StringMatchers.containsStrings;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;

import net.saff.glinda.book.StaticParser;
import net.saff.glinda.book.TypeSpecificParser;
import net.saff.glinda.interpretation.finding.Function;
import net.saff.glinda.interpretation.finding.NoSuchLoqMethodException;
import net.saff.glinda.interpretation.finding.UnspecifiedStringTarget;
import net.saff.glinda.interpretation.finding.Function.DelayedDelegate;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.stubbedtheories.DontRun;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesFunction extends LoqBookDataPoints {
  private static GlindaMethod TOSTRING;
  static {
    try {
      TOSTRING = GlindaMethod.forMethod(Object.class.getMethod("toString"), null);
    } catch (Exception e) {
      TOSTRING = null;
    }
  }

  @RunWith(DontRun.class)
  public static class WantsOutputStream {
    @Test
    public void test(@SuppressWarnings("unused")
    OutputStream x) {
    }
  }

  @Test
  public void indicateWhatsWrongWithArguments() {
    try {
      Function.obvious(new WantsOutputStream()).invoke(asList("a"));
    } catch (Exception e) {
      assertThat(e.getCause().getMessage(), containsString("String"));
    }
  }

  @RunWith(DontRun.class)
  public static class BlowsUp {
    @Test
    public void test(@SuppressWarnings("unused")
    int x) {
      throw new RuntimeException();
    }
  }

  @Theory
  public void indicatesParametersWhenSomethingGoesWrong(int x) {
    try {
      Function.obvious(new BlowsUp()).invoke(asList("" + x));
    } catch (Exception e) {
      assertThat(e.getMessage(), containsStrings(x));
    }
  }

  @Test
  public void executeDelayedDelegate() throws SecurityException,
      NoSuchMethodException, NoSuchLoqMethodException,
      LoqCommandExecutionException {
    final StringBuffer target = new StringBuffer("abc");
    TypeSpecificParserFactory argumentParser = new TypeSpecificParserFactory() {
      public TypeSpecificParser parserFor(final Type type) {
        return new TypeSpecificParser() {

          public Object parse(String string) {
            if (type == StringBuffer.class) return target;
            return string;
          }
        };
      }
    };
    Function delegate =
        Function.delayedDelegate(new UnspecifiedStringTarget(null,
            argumentParser), StringBuffer.class.getMethod("append",
            String.class), argumentParser);
    delegate.invoke(asList("abc", "def"));
    assertThat(target.toString(), is("abcdef"));
  }

  @Test
  public void computeParamTypesOnDelayedDelegate() throws SecurityException {
    assertThat(Function.delayedDelegate(null, TOSTRING).getParamTypes().size(),
        is(0));
  }

  @Test
  public void computeParamTypesOnConcreteFunction() throws SecurityException {
    assertThat(Function.of(new UnspecifiedStringTarget(null), TOSTRING)
        .getParamTypes().size(), is(0));
  }

  @Test
  public void computeTargetOnReverse() throws SecurityException,
      NoSuchMethodException, ParseException {
    TypeSpecificParserFactory argumentParser = new TypeSpecificParserFactory() {

      public TypeSpecificParser parserFor(Type type) {
        return new TypeSpecificParser() {
          public Object parse(String string) {
            return new StringBuffer(string);
          }
        };
      }
    };
    DelayedDelegate delegate =
        Function.delayedDelegate(new UnspecifiedStringTarget(null,
            argumentParser), StringBuffer.class.getMethod("reverse"),
            argumentParser);
    assertThat(delegate.stringTarget(asList("abc")).toString(), is("abc"));
  }

  public static class DoubleTaker {
    public void takesADouble(@SuppressWarnings("unused")
    double d) {
      // example for below
    }
  }

  @Test(expected = LoqCommandExecutionException.class)
  public void reportDoubleValueMismatches() throws SecurityException,
      NoSuchMethodException, LoqCommandExecutionException {
    Function function =
        Function.of(new UnspecifiedStringTarget(new DoubleTaker(),
            new StaticParser()), DoubleTaker.class.getMethod("takesADouble",
            double.class), new StaticParser());
    function.invoke(asList("badDouble"));
  }
}

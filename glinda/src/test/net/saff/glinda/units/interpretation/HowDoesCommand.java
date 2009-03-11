package test.net.saff.glinda.units.interpretation;

import static net.saff.glinda.ideas.correspondent.StringMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import net.saff.glinda.interpretation.declaring.Command;
import net.saff.glinda.interpretation.finding.Function;
import net.saff.glinda.interpretation.finding.NoSuchLoqMethodException;
import net.saff.glinda.interpretation.finding.UnspecifiedStringTarget;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.stubbedtheories.ParameterTypes;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class)
public class HowDoesCommand extends LoqBookDataPoints {
  @SuppressWarnings("unused")
  public static class Tracker {
    private int arity;

    public void track(String oneThing) {
      arity = 1;
    }

    public void track(String oneThing, String twoThings) {
      arity = 2;
    }
  }

  @Test
  public void alwaysTreatBracketedExpressionsAsOneWord()
      throws NoSuchLoqMethodException, LoqCommandExecutionException {
    Tracker tracker = new Tracker();
    Command.required("track", "[a", "b]").runOn(
        new UnspecifiedStringTarget(tracker));
    assertThat(tracker.arity, is(1));
  }

  @Theory
  public void includeUnfindableMethodNameInException(String methodName) {
    try {
      Command.required(methodName).runOn(
          new UnspecifiedStringTarget(new Object()));
      // only interested when an exception is thrown
      assumeTrue(false);
    } catch (Exception e) {
      assertThat(e.getMessage(), containsString(methodName));
    }
  }

  @SuppressWarnings("unused")
  public static class TrackerZeroThree {
    public void track() {
    }

    public void track(String oneThing, String twoThings, String threeThings) {
    }
  }

  @Test
  public void dontChooseZeroArgMethodWhenArgsExist() {
    Function function =
        Command.required("track", "something").findFunction(
            new UnspecifiedStringTarget(new TrackerZeroThree()));
    assertThat(function.stringArgumentCountNeeded(), is(3));
  }

  @Test
  public void chooseAGoodAppendMethod() throws SecurityException,
      NoSuchMethodException {
    Function function =
        function(StringBuffer.class.getMethod("append", String.class),
            "append", "abc", "def");
    assertEquals(new ParameterTypes(String.class), function.getParamTypes());
  }

  @Test
  public void importReverseCommand() throws SecurityException,
      NoSuchMethodException {
    Function function =
        function(StringBuffer.class.getMethod("reverse"), "reverse", "abc");

    assertEquals(ParameterTypes.NONE, function.getParamTypes());
  }

  private Function function(Method method, String... command) {
    ArrayList<GlindaMethod> imports = new ArrayList<GlindaMethod>();
    imports.add(GlindaMethod.forMethod(method, null));

    return Command.required(imports, Arrays.asList(command)).findFunction(
        new UnspecifiedStringTarget(null));
  }
}

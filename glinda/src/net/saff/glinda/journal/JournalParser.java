package net.saff.glinda.journal;

import static java.util.Arrays.asList;

import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.interpretation.declaring.Command;
import net.saff.glinda.interpretation.finding.CommandStrings;
import net.saff.glinda.interpretation.finding.Function;
import net.saff.glinda.interpretation.finding.NoSuchLoqMethodException;
import net.saff.glinda.interpretation.finding.StringTarget;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.time.GlindaTimePoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JournalParser<T> implements Serializable {
	private static final long serialVersionUID = 1L;

public static abstract class RegexInterpreter implements LineInterpreter {
    private Pattern pattern;

    public RegexInterpreter(Pattern pattern) {
      this.pattern = pattern;
    }

    public abstract void interpret(Matcher matcher) throws Exception;

    public boolean interpretsSuccessfully(String line) throws Exception {
      Matcher matcher = pattern.matcher(line);
      if (matcher.matches()) {
        interpret(matcher);
        return true;
      }

      return false;
    }
  }

  private static final Pattern TARGET_PATTERN =
      Pattern.compile("##target (.*)");
  protected StringTarget<T> target = new StringTarget<T>(null);
  private final List<GlindaMethod> imports = new ArrayList<GlindaMethod>();
  protected boolean ignoreProblems;
  private List<? extends LineInterpreter> interpreters = null;

  public JournalParser(boolean ignoreProblems2) {
    ignoreProblems = ignoreProblems2;
    // TODO: DUP with above
    setTarget(null);
  }

  public void nowIs(GlindaTimePoint now) throws LoqCommandExecutionException {
    getTarget().setReflectively("setNow", GlindaTimePoint.class, now);
  }

  public void setCorrespondent(Correspondent correspondent)
      throws LoqCommandExecutionException {
    getTarget().setReflectively("setCorrespondent", Correspondent.class,
        correspondent);
  }

  public String output(CommandStrings strings)
      throws LoqCommandExecutionException {
    return makeCommand(strings).output(getTarget());
  }

  public Command makeCommand(CommandStrings strings) {
    return Command.required(getImports(), strings.toList());
  }

  public String invoke(GlindaTimePoint now, Correspondent correspondent,
      CommandStrings strings) throws LoqCommandExecutionException {
    nowIs(now);
    setCorrespondent(correspondent);
    return output(strings);
  }

  public StringTarget<T> getTarget() {
    return target;
  }

  public void a(String line) throws LoqCommandExecutionException {
    if (!line.startsWith("#")) return;
    try {
      for (LineInterpreter each : interpreters)
        if (each.interpretsSuccessfully(line)) return;
    } catch (Exception e) {
      throw new LoqCommandExecutionException(e, asList(line));
    }
  }

  private List<? extends LineInterpreter> lineInterpreters(TypeSpecificParserFactory parser) {
    return asList(setTargetInterpreter(), addImportInterpreter(parser),
        executeCommandInterpeter(), flagProblem());
  }

  private LineInterpreter flagProblem() {
    return new LineInterpreter() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public boolean interpretsSuccessfully(String line) throws Exception {
        if (ignoreProblems) return true;
        throw new LoqCommandExecutionException("Could not interpret line: "
            + line);
      }
    };
  }

  private LineInterpreter executeCommandInterpeter() {
    return new LineInterpreter() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public boolean interpretsSuccessfully(String line) throws Exception {
        try {
          Command command = commandForLine(line);
          Function function = command.findFunction(getTarget());
          if (function.isUseless()) return false;
          function.invoke(command.argList());
          return true;
        } catch (NoSuchLoqMethodException e) {
          return false;
        }
      }
    };
  }

  private LineInterpreter addImportInterpreter(final TypeSpecificParserFactory parser) {
    return new LineInterpreter() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public boolean interpretsSuccessfully(String line) throws Exception {
        if (!line.startsWith("##import")) return false;
        int lastDot = line.lastIndexOf('.');
        String className = line.substring(line.indexOf(' ') + 1, lastDot);
        String methodName = line.substring(lastDot + 1);
        getImports().add(GlindaMethod.forMethod(new ImportRequest(
            Class.forName(className), methodName).findMethodWithFewestParams(), parser));
        return true;
      }
    };
  }

  private RegexInterpreter setTargetInterpreter() {
    return new RegexInterpreter(TARGET_PATTERN) {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @SuppressWarnings("unchecked")
      @Override
      public void interpret(Matcher matcher) throws Exception {
        setTarget((T) Class.forName(matcher.group(1)).newInstance());
      }
    };
  }

  protected Command commandForLine(String line) {
    int firstSpace = line.indexOf(' ');
    String rest;
    if (firstSpace == -1) {
      rest = null;
      firstSpace = line.length();
    } else
      rest = line.substring(firstSpace + 1, line.length()).trim();

    String firstString = line.substring(1, firstSpace);
    LinkedList<String> strings = new LinkedList<String>();
    strings.add(firstString);
    strings.add(rest);
    return Command.required(getImports(), strings);
  }

  public void setTarget(T newTarget) {
    target = new StringTarget<T>(newTarget);
    interpreters = lineInterpreters(target.getParser());
  }

  public List<GlindaMethod> getImports() {
    return imports;
  }
}

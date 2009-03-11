package net.saff.glinda.interpretation.finding;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.interpretation.invoking.ArgumentStrings;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.stubbedtheories.ParameterTypes;

public abstract class Function {
  public static Function of(StringTarget<?> target, GlindaMethod method) {
    return new ConcreteFunction(target, method);
  }

  // TODO: pass-through
  public static Function of(StringTarget<Object> target, Method method,
      TypeSpecificParserFactory parser) {
    return of(target, GlindaMethod.forMethod(method, parser));
  }

  public static DelayedDelegate delayedDelegate(StringTarget<?> target,
      GlindaMethod method) {
    return new DelayedDelegate(target, method);
  }

  // TODO: pass-through
  public static DelayedDelegate delayedDelegate(StringTarget<Object> target,
      Method method, TypeSpecificParserFactory parser) {
    return delayedDelegate(target, GlindaMethod.forMethod(method, parser));
  }

  public static Function obvious(Object target) {
    return Function.of(new UnspecifiedStringTarget(target), target.getClass()
        .getDeclaredMethods()[0], UnspecifiedStringTarget.parserFor(target));
  }

  public static Function useless(final boolean ignoreProblems,
      final String methodName) {
    return new Function() {
      @Override
      public int stringArgumentCountNeeded() {
        return Integer.MAX_VALUE;
      }

      @Override
      public Object invoke(List<String> args) throws NoSuchLoqMethodException {
        if (ignoreProblems) return null;
        throw new NoSuchLoqMethodException(methodName);
      }

      @Override
      public ParameterTypes getParamTypes() {
        return null;
      }

      @Override
      public boolean isUseless() {
        return true;
      }
    };
  }

  static abstract class MethodBasedFunction extends Function {
    private final GlindaMethod method;

    public MethodBasedFunction(GlindaMethod method) {
      this.method = method;
    }

    protected abstract StringTarget<?> stringTarget(List<String> args)
        throws ParseException;

    protected abstract ArgumentStrings stringsForArguments(List<String> args);

    @Override
    public ParameterTypes getParamTypes() {
      return method.getParameterTypes();
    }

    protected GlindaMethod getMethod() {
      return method;
    }

    protected ArgumentStrings collapseToLength(List<String> args) {
      return new ArgumentStrings(args, stringArgumentCountNeeded());
    }

    public Object invoke(List<String> args)
        throws LoqCommandExecutionException, NoSuchLoqMethodException {
      try {
        return stringTarget(args).invoke(method, stringsForArguments(args));
      } catch (Exception e) {
        throw new LoqCommandExecutionException(e, args);
      }
    }

    @Override
    public boolean isUseless() {
      return false;
    }
  }

  private static class ConcreteFunction extends MethodBasedFunction {
    final StringTarget<?> target;

    ConcreteFunction(StringTarget<?> target2, GlindaMethod method) {
      super(method);
      this.target = target2;
    }

    public int stringArgumentCountNeeded() {
      return getParamTypes().size();
    }

    @Override
    public String toString() {
      return String.format("%s.%s", target, getMethod().getName());
    }

    @Override
    protected ArgumentStrings stringsForArguments(List<String> args) {
      return collapseToLength(args);
    }

    @Override
    protected StringTarget<?> stringTarget(List<String> args) {
      return target;
    }
  }

  public static class DelayedDelegate extends MethodBasedFunction {
    private final StringTarget<?> target;

    public DelayedDelegate(StringTarget<?> target2, GlindaMethod method) {
      super(method);
      this.target = target2;
    }

    public int stringArgumentCountNeeded() {
      return getParamTypes().size() + 1;
    }

    @Override
    protected ArgumentStrings stringsForArguments(List<String> args) {
      return collapseToLength(args).cdr();
    }

    @Override
    public StringTarget<Object> stringTarget(List<String> args)
        throws ParseException {
      return target.retarget(collapseToLength(args).car(), getMethod()
          .getDeclaringClass());
    }
  }

  public abstract int stringArgumentCountNeeded();

  public abstract Object invoke(List<String> list)
      throws LoqCommandExecutionException;

  public final int argLengthDifference(int desiredLength) {
    return Math.abs(desiredLength - stringArgumentCountNeeded());
  }

  public abstract ParameterTypes getParamTypes();

  public abstract boolean isUseless();

  public void invoke(CommandStrings argList)
      throws LoqCommandExecutionException {
    invoke(argList.toList());
  }
}

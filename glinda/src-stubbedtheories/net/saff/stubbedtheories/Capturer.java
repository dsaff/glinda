package net.saff.stubbedtheories;

import java.lang.reflect.Method;

import net.saff.stubbedtheories.guessing.MethodCall;

import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.lib.legacy.ClassImposteriser;

public class Capturer<T> implements Invokable {
  public interface DummyInterface {

  }

  private String log = "";

  private final Object delegate;

  private final Class<T> type;

  public Capturer(Object delegate, Class<T> type) {
    this.type = type;
    this.delegate = delegate;
  }

  @SuppressWarnings("unchecked")
  public static <T> Capturer<T> forObject(T delegate) {
    return (Capturer<T>) forObject(delegate, delegate.getClass());
  }

  public T getProxy() {
    return ClassImposteriser.INSTANCE.imposterise(this, type,
        DummyInterface.class);
  }

  public String log() {
    return log;
  }

  public Object invoke(Method method, Object[] args)
      throws Throwable {
    log += new MethodCall(method, args).toString();
    return method.invoke(delegate, args);
  }

  public Object invoke(Invocation invocation) throws Throwable {
    log +=
        new MethodCall(invocation.getInvokedMethod(), invocation
            .getParametersAsArray()).toString();
    return invocation.applyTo(delegate);
  }

  public static <T> Capturer<T> forObject(Object obj, Class<T> type) {
    return new Capturer<T>(obj, type);
  }
}

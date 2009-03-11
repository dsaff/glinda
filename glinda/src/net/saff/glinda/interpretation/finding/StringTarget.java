package net.saff.glinda.interpretation.finding;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.interpretation.invoking.ArgumentStrings;
import net.saff.glinda.interpretation.invoking.Delegate;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;

public class StringTarget<T> implements Serializable {
	private final T target;
	private Map<String, List<GlindaMethod>> cachedMethods = null;
	// TODO: DUP type
	protected Map<Class<?>, Object> delegates = new HashMap<Class<?>, Object>();
	private final TypeSpecificParserFactory parser;

	// TODO: unstatic
	// TODO: DUP type
	private static Map<Object, Map<Class<?>, Object>> delegateCache = new HashMap<Object, Map<Class<?>, Object>>();

	public StringTarget(T target, TypeSpecificParserFactory parser2) {
		this.target = target;
		parser = parser2;
		// TODO: DUP below
		addDelegates(target);
	}

	private void addDelegates(T target) {
		if (target == null)
			return;
		if (!delegateCache.containsKey(target)) {
			HashMap<Class<?>, Object> freshMap = new HashMap<Class<?>, Object>();
			Class<? extends Object> type = target.getClass();
			while (type != null && type != Object.class) {
				freshMap.put(type, target);
				type = type.getSuperclass();
			}
			delegateCache.put(target, freshMap);
		}
		delegates.putAll(delegateCache.get(target));
	}

	public StringTarget(T target) {
		this(target, UnspecifiedStringTarget.parserFor(target));
	}

	public T getTarget() {
		return target;
	}

	public List<GlindaMethod> availableMethods(String lowerCaseName) {
		if (getTarget() == null)
			return Collections.emptyList();
		if (cachedMethods == null)
			computeCachedMethods();
		List<GlindaMethod> list = cachedMethods.get(lowerCaseName);
		if (list == null)
			return Collections.emptyList();
		return list;
	}

	private void computeCachedMethods() {
		cachedMethods = new HashMap<String, List<GlindaMethod>>();
		addCachedMethods(getTarget());
	}

	@SuppressWarnings("unchecked")
	private void addCachedMethods(Object target2) {
		for (Method each : target2.getClass().getMethods()) {
			addCachedMethod(each, each.getName().toLowerCase());
			if (each.getAnnotation(Delegate.class) != null) {
				try {
					T delegate = (T) each.invoke(target2);
					addCachedMethods(delegate);
					addDelegates(delegate);
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}

	private void addCachedMethod(Method each, String lowerCase) {
		if (!cachedMethods.containsKey(lowerCase))
			cachedMethods.put(lowerCase, new ArrayList<GlindaMethod>());
		cachedMethods.get(lowerCase).add(GlindaMethod.forMethod(each, getParser()));
	}

	StringTarget<Object> retarget(String targetString, Class<?> targetClass)
			throws ParseException {
		return new UnspecifiedStringTarget(getParser().parserFor(targetClass)
				.parse(targetString), getParser());
	}

	public <U> void setReflectively(String methodName, Class<U> type, U value)
			throws LoqCommandExecutionException {
		try {
			Method method = getTarget().getClass().getMethod(methodName, type);
			method.invoke(getTarget(), value);
		} catch (NoSuchMethodException e) {
			return;
		} catch (Exception e) {
			throw new LoqCommandExecutionException(e);
		}
	}

	// TODO: where is parser used?
	Object invoke(GlindaMethod method, ArgumentStrings argumentStrings)
			throws Exception {
		return method.invokeChecked(getTarget(method), method
				.parse(argumentStrings));
	}

	private Object getTarget(GlindaMethod method) {
		Object object = delegates.get(method.getDeclaringClass());
		if (object == null)
			// TODO: test in
			throw new RuntimeException("Can't find "
					+ method.getDeclaringClass() + " in " + delegates.keySet());
		return object;
	}

	public TypeSpecificParserFactory getParser() {
		return parser;
	}

	// TODO (Apr 20, 2008 1:59:43 AM): does toString need to be here?

}

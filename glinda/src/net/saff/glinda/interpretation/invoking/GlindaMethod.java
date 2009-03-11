/**
 * 
 */
package net.saff.glinda.interpretation.invoking;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import net.saff.glinda.book.TypeSpecificParser;
import net.saff.stubbedtheories.ParameterTypes;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class GlindaMethod implements Serializable {
	private static Map<Method, Map<TypeSpecificParserFactory, GlindaMethod>> glindaMethodCache = new HashMap<Method, Map<TypeSpecificParserFactory, GlindaMethod>>();

	public static GlindaMethod forMethod(Method method, TypeSpecificParserFactory parser) {
		// TODO: un-staticize
		// TODO: experiment, and show that this is useful
		if (!glindaMethodCache.containsKey(method))
			glindaMethodCache.put(method,
					new HashMap<TypeSpecificParserFactory, GlindaMethod>());
		Map<TypeSpecificParserFactory, GlindaMethod> parserCache = glindaMethodCache
				.get(method);
		if (!parserCache.containsKey(parser))
			parserCache.put(parser, new GlindaMethod(method, parser));
		return parserCache.get(parser);
	}

	transient private boolean triedFastMethod = false;

	transient private FastMethod fastMethod = null;

	private Type[] cachedGenericParameterTypes = null;

	private String cachedLowerCaseName = null;

	private final ParameterTypes parameterTypes;

	private TypeSpecificParser[] cachedTypeSpecificParsers = null;

	private final TypeSpecificParserFactory parser;

	private final Class<?> declaringClass;

	private final String name;

	private transient Method cachedMethod = null;

	private GlindaMethod(Method method, TypeSpecificParserFactory parser) {
		this.parser = parser;
		this.fastMethod = fastMethod(method);
		this.declaringClass = method.getDeclaringClass();
		this.parameterTypes = new ParameterTypes(method.getParameterTypes());
		this.name = method.getName();
		this.cachedMethod = null;
	}

	private FastMethod fastMethod(Method method) {
		FastClass fc = FastClass.create(method.getDeclaringClass());
		if (fc.getIndex(method.getName(), method.getParameterTypes()) < 0)
			return null;
		return fc.getMethod(method);
	}

	public int parameterCount() {
		return getParameterTypes().size();
	}

	public ParameterTypes getParameterTypes() {
		return parameterTypes;
	}

	public Type[] getGenericParameterTypes() {
		if (cachedGenericParameterTypes == null)
			cachedGenericParameterTypes = getMethod()
					.getGenericParameterTypes();
		return cachedGenericParameterTypes;
	}

	public Class<?> getDeclaringClass() {
		return getMethod().getDeclaringClass();
	}

	public String getName() {
		return getMethod().getName();
	}

	public boolean nameMatches(String thisMethodName) {
		if (cachedLowerCaseName == null)
			cachedLowerCaseName = getMethod().getName().toLowerCase();
		return cachedLowerCaseName.equals(thisMethodName)
				&& getDeclaringClass() != Object.class;
	}

	public Object invokeChecked(Object target, Object[] parsedArgs)
			throws Exception {
		try {
			if (getFastMethod() != null)
				// TODO (Apr 20, 2008 2:00:05 AM): pass-through?

				return getFastMethod().invoke(target, parsedArgs);
			else
				return getMethod().invoke(target, parsedArgs);
		} catch (Exception e) {
			getParameterTypes().checkParams(parsedArgs);
			throw e;
		}
	}

	private FastMethod getFastMethod() {
		if (triedFastMethod == false) {
			triedFastMethod = true;
			fastMethod = fastMethod(getMethod());
		}
		return fastMethod;
	}

	public Object[] parse(ArgumentStrings argumentStrings)
			throws ParseException {
		// TODO: cycle?
		TypeSpecificParser[] parsers = getTypeSpecificParsers();
		int size = parsers.length;
		Object[] realArgs = new Object[size];

		int i = 0;
		for (String each : argumentStrings.argsList) {
			realArgs[i] = parsers[i].parse(each);
			i++;
		}
		return realArgs;
	}

	private TypeSpecificParser[] getTypeSpecificParsers() {
		if (cachedTypeSpecificParsers == null) {
			Type[] genericParameterTypes = getGenericParameterTypes();
			cachedTypeSpecificParsers = new TypeSpecificParser[genericParameterTypes.length];
			for (int i = 0; i < genericParameterTypes.length; i++) {
				Type type = genericParameterTypes[i];
				cachedTypeSpecificParsers[i] = parser.parserFor(type);
			}
		}
		return cachedTypeSpecificParsers;
	}

	private Method getMethod() {
		if (cachedMethod == null)
			cachedMethod = computeMethod();
		return cachedMethod;
	}

	private Method computeMethod() {
		try {
			return declaringClass.getMethod(name,
					parameterTypes.rawParameterTypes);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Serialized method information points to invalid method", e);
		}
	}
	
	@Override
	public String toString() {
	  return getMethod().toString();
	}
	
	@Override
	public boolean equals(Object obj) {
	  return getMethod().equals(((GlindaMethod) obj).getMethod());
	}
}

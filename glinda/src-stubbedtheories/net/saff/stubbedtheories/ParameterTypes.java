package net.saff.stubbedtheories;

import java.io.Serializable;
import java.util.Arrays;

public class ParameterTypes implements ConstructorEquality, Serializable {
	// TODO: encapsulate
	public Class<?>[] parameterTypes;
	public Class<?>[] rawParameterTypes;
	public static final ParameterTypes NONE = new ParameterTypes();

	public ParameterTypes(Class<?>... types) {
		rawParameterTypes = types;
		parameterTypes = new Class<?>[types.length];
		for (int i = 0; i < types.length; i++)
			parameterTypes[i] = objectType(types[i]);
	}

	public void checkParams(Object... params) {
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];

			Class<?> methodType = parameterTypes[i];
			if (param != null && !methodType.isInstance(param))
				throw new IllegalArgumentException(String.format(
						"Parameter %s: expected %s, got %s", i, methodType
								.getSimpleName(), param.getClass()
								.getSimpleName()));
		}
	}

	private Class<?> objectType(Class<?> methodType) {
		if (!methodType.isPrimitive())
			return methodType;
		if (methodType.equals(double.class))
			return Double.class;
		if (methodType.equals(int.class))
			return Integer.class;
		if (methodType.equals(char.class))
			return Character.class;
		if (methodType.equals(short.class))
			return Short.class;
		if (methodType.equals(byte.class))
			return Byte.class;
		if (methodType.equals(long.class))
			return Long.class;
		if (methodType.equals(float.class))
			return Float.class;
		if (methodType.equals(boolean.class))
			return Boolean.class;
		return methodType;
	}

	public int size() {
		return parameterTypes.length;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ParameterTypes))
			return false;
		ParameterTypes types = (ParameterTypes) obj;
		return Arrays.equals(parameterTypes, types.parameterTypes);
	}
}
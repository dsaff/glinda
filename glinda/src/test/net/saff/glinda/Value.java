package test.net.saff.glinda;

import java.lang.reflect.Modifier;

import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.lib.legacy.ClassImposteriser;

public class Value {
	public static <T> T of(T proxied) {
		return valueOf("[[" + proxied + "]]", proxied);
	}

	@SuppressWarnings("unchecked") private static <T> T valueOf(final String prefix,
			final T proxied) {
		if (Modifier.isFinal(proxied.getClass().getModifiers()))
			return proxied;
		return (T) ClassImposteriser.INSTANCE.imposterise(new Invokable() {
			public Object invoke(Invocation invocation) throws Throwable {
				String name = invocation.getInvokedMethod().getName();
				if (name.equals("toString"))
					return prefix + " = " + proxied;
				return valueOf(prefix + "." + name + "()", invocation
						.applyTo(proxied));
			}
		}, proxied.getClass());
	}

}

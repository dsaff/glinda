/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under GNU General Public License 2.0 (http://www.gnu.org/licenses/gpl.html)
 */
package org.junit.extensions.cpsuite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class ClasspathSuite extends Suite {

	private static final boolean DEFAULT_INCLUDE_JARS = false;
	private static final SuiteType[] DEFAULT_SUITE_TYPES = new SuiteType[] { SuiteType.TEST_CLASSES };

	/**
	 * The <code>ClassnameFilters</code> annotation specifies a set of regex
	 * expressions for all test classes (ie. their qualified names) to include
	 * in the test run. When the annotation is missing, all test classes in all
	 * packages will be run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClassnameFilters {
		public String[] value();
	}

	/**
	 * The <code>IncludeJars</code> annotation specifies if Jars should be
	 * searched in or not. If the annotation is missing Jars are not bein
	 * searched.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface IncludeJars {
		public boolean value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteTypes {
		public SuiteType[] value();
	}

	/**
	 * Internal use only.
	 */
	public ClasspathSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(builder, klass, getSortedTestclasses(klass));
	}

	//	public ClasspathSuite(Class<?> klass) throws InitializationError {
	//		this(klass, getSortedTestclasses(klass));
	//	}

	//	/**
	//	 * Internal use only.
	//	 */
	//	private ClasspathSuite(Class<?> klass, Class<?>[] testclasses)
	//			throws InitializationError {
	//		super(klass, Request.classes(klass.getName(), testclasses).getRunner());
	//	}

	private static Class<?>[] getSortedTestclasses(Class<?> suiteClass) {
		List<Class<?>> testclasses = getTestclasses(suiteClass);
		Collections.sort(testclasses, getClassComparator());
		return testclasses.toArray(new Class[testclasses.size()]);
	}

	private static List<Class<?>> getTestclasses(Class<?> suiteClass) {
		ClassTester tester = new ClasspathSuiteTester(
				getSearchInJars(suiteClass), getClassnameFilters(suiteClass),
				getSuiteTypes(suiteClass));
		return new ClasspathClassesFinder(tester).find();
	}

	private static Comparator<Class<?>> getClassComparator() {
		return new Comparator<Class<?>>() {
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};
	}

	private static String[] getClassnameFilters(Class<?> suiteClass) {
		ClassnameFilters filtersAnnotation = suiteClass
				.getAnnotation(ClassnameFilters.class);
		if (filtersAnnotation == null) {
			return null;
		}
		return filtersAnnotation.value();
	}

	private static boolean getSearchInJars(Class<?> suiteClass) {
		IncludeJars includeJarsAnnotation = suiteClass
				.getAnnotation(IncludeJars.class);
		if (includeJarsAnnotation == null) {
			return DEFAULT_INCLUDE_JARS;
		}
		return includeJarsAnnotation.value();
	}

	private static SuiteType[] getSuiteTypes(Class<?> suiteClass) {
		SuiteTypes suiteTypesAnnotation = suiteClass
				.getAnnotation(SuiteTypes.class);
		if (suiteTypesAnnotation == null) {
			return DEFAULT_SUITE_TYPES;
		}
		return suiteTypesAnnotation.value();
	}
}

package test.net.saff;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.SuiteType;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.extensions.cpsuite.ClasspathSuite.IncludeJars;
import org.junit.extensions.cpsuite.ClasspathSuite.SuiteTypes;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)
@SuiteTypes({SuiteType.RUN_WITH_CLASSES, SuiteType.TEST_CLASSES})
@ClassnameFilters("test.*")
@IncludeJars(true)
public class HowDoesEverything {

}
package test.net.saff.stubbedtheories;

import net.saff.stubbedtheories.guessing.StringableObject;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestStringableObject {
	@DataPoint public static double[] doubles = new double[] {1.0};
	
	@Theory public void stringableObjectReturnsNormally(Object obj) {
		new StringableObject(obj).stringableObject();
		// expect normal return
	}
}

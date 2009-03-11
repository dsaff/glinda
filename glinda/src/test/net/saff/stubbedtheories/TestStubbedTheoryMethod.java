package test.net.saff.stubbedtheories;

import net.saff.stubbedtheories.ParameterTypes;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

@RunWith(Theories.class) public class TestStubbedTheoryMethod extends
		LoqBookDataPoints {
	@Test public void allPrimitiveTypesPassCheck() {
		new ParameterTypes(int.class).checkParams(1);
		new ParameterTypes(char.class).checkParams('a');
		new ParameterTypes(short.class).checkParams((short) 1);
		new ParameterTypes(byte.class).checkParams((byte) 1);
		new ParameterTypes(long.class).checkParams(1L);
		new ParameterTypes(float.class).checkParams(1.0f);
		new ParameterTypes(double.class).checkParams(1.0);
		new ParameterTypes(boolean.class).checkParams(true);
	}
}

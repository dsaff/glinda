package test.net.saff.glinda.user.k_pragmas;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class HowDoesTarget extends CondCorrespondentOnDiskTest {	
	public static class CountIncrements {
		private int count = 0;
		
		public void increment() {
			count++;
		}
		
		public String value() {
			return "[" + count + "]";
		}
	}
	
	@Test public void includeOtherTargets() {
		r("##target " + CountIncrements.class.getName());
		r("#increment");
		run("value");
		w("value: [1]");
	}
}

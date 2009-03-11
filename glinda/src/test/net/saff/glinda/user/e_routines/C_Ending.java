/**
 * 
 */
package test.net.saff.glinda.user.e_routines;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class C_Ending extends CondCorrespondentOnDiskTest {
	@Test
	public void makeRoutineDisappear() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startRoutine BrushTeeth each day at 9pm");
		r("#endRoutine BrushTeeth");
		nowIs("2007-01-02 21:01:00");

		run("status");
		w("status:");
		done();
	}
}	

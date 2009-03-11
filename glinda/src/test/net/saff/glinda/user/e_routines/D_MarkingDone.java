/**
 * 
 */
package test.net.saff.glinda.user.e_routines;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class D_MarkingDone extends CondCorrespondentOnDiskTest {
	@Test
	public void dropRoutineRequirementsWhenRoutineMarkedDone() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startRoutine BrushTeeth each day at 9pm");
		r("#priorTo BrushTeeth FindToothbrush");
		r("#NOW 2007-01-02 22:00:00");
		r("#done BrushTeeth");
		nowIs("2007-01-03 22:01:00");

		run("status");
		w("status:");
		w("! r:BrushTeeth (since 2007-01-03 21:00:00)");
		w("! p:FindToothbrush");
		done();
	}
}	

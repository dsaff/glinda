/**
 * 
 */
package test.net.saff.glinda.user.e_routines;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class A_Starting extends CondCorrespondentOnDiskTest {
	@Test
	public void makeRoutineNotDone() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startRoutine BrushTeeth each day at 9pm");
		r("#NOW 2007-01-02 12:00:10");
		r("#done BrushTeeth");
		nowIs("2007-01-02 21:01:00");

		run("status");
		w("status:");
		w("! r:BrushTeeth (since 2007-01-02 21:00:00)");
	}

	@Test
	public void putRoutinesFirst() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startRoutine BrushTeeth each day at 9pm");
		r("#startProject AProject");
		r("#track AGoal 0");
		r("#NOW 2007-01-02 12:00:10");
		r("#done BrushTeeth");
		nowIs("2007-01-02 21:01:00");

		run("status");
		w("status:");
		w("! r:BrushTeeth (since 2007-01-02 21:00:00)");
		w("> AGoal (was null, is 0)");
		w("! p:AProject");
	}

	@Test
	public void makeRoutineDone() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startRoutine BrushTeeth each day at 9pm");
		r("#NOW 2007-01-02 12:00:10");
		r("#done BrushTeeth");
		nowIs("2007-01-02 20:01:00");

		run("status");
		w("status:");
		w("> r:BrushTeeth (until 2007-01-02 21:00:00)");
	}

	@Test
	public void doesntBlockSoftParentWhenDone() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startRoutine TakeOutRecycling each day at 11am");
		r("#priorTo Jump TakeOutRecycling");
		r("#done TakeOutRecycling");
		r("#NOW 2007-01-02 12:00:10");
		nowIs("2007-01-02 12:00:20");

		run("status", "-actionItemsFirst");
		w("status:");
		w("! p:Jump");
		w("> r:TakeOutRecycling (until 2007-01-03 11:00:00)");
	}

	@Test(expected = Throwable.class)
	public void gripeIfAnotherTypeExists() {
		r("#NOW 2007-01-02 12:00:00");
		r("#startGoal TakeOutRecycling");
		r("#startRoutine TakeOutRecycling each 2 weeks starting 11pm");
		nowIs("2007-01-03 12:00:20");

		run("status");
	}

	@Test
	public void showIgnoredLinkWhenActive() {
		r("#NOW 2008-01-30 09:00:52");
		r("#startRoutine PutOutOrganicsBin each day at 5am");
		r("#priorTo PutOutOrganicsBin DoSomethingElse");
		r("#wait DoSomethingElse 20 days");
		nowIs("2008-01-31 09:00:00");

		run("status");
		w("status:");
		w("! r:PutOutOrganicsBin [-> DoSomethingElse] (since 2008-01-31 05:00:00)");
		w("> p:DoSomethingElse (waiting until 2008-02-19 09:00:52)");
	}

	@Test
	public void blockParent() {
		r("#NOW 1998-01-03 12:00:00");
		r("#startRoutine TakeOutRecycling each month on the 22nd at 3pm");
		r("#NOW 1998-01-22 15:00:01");
		r("#done TakeOutRecycling");
		r("#NOW 1998-01-22 15:00:02");
		r("#nextStep EatACan TakeOutRecycling");
		nowIs("1998-01-22 15:00:03");

		run("status");
		w("status:");
		w("> r:TakeOutRecycling (until 1998-02-22 15:00:00)");
		w("> p:EatACan (next step: r:TakeOutRecycling)");
	}
}

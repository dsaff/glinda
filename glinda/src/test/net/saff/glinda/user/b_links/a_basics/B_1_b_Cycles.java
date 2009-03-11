/**
 * 
 */
package test.net.saff.glinda.user.b_links.a_basics;

import net.saff.glinda.projects.core.Project;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class B_1_b_Cycles extends CondCorrespondentOnDiskTest {
	@Test
	public void breakOldestRequirement() {
		r("#now 2007-01-01 12:00:00");
		r("#nextStep Z B");
		r("#nextStep B C");
		r("#nextStep C Z");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:Z [=> B => C => Z!] <= C <= B <= [Broke cycle]");
		w("> p:B (next step: p:C)");
		w("> p:C (next step: p:Z)");
	}

	@Test
	public void displayCycle() {
		r("#startProject FixFloor");
		r("#nextStep FixFloor FixFloor");

		runStatus("-actionItemsFirst", "-withTimes", "-withParents");
		w("! p:FixFloor [=> FixFloor!] <= [Broke cycle]");
	}

	@Test
	public void displayCycleThroughSecondChild() {
		r("#now 2008-01-03 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor A");
		r("#nextStep FixFloor B");
		r("#nextStep B FixFloor");
		r("#wait A 1d");
		nowIs("2008-01-03 12:00:00");

		runStatus("-actionItemsFirst", "-withTimes", "-withParents");
		w("! p:FixFloor");
		w("  [-> A]");
		w("  [=> B => FixFloor!]");
		w("  <= B <= [Broke cycle]");
		w("> p:A (waiting until 2008-01-04 12:00:00)");
		w("> p:B (next step: p:FixFloor)");
	}

	@Test
	public void indicateCycleClearly() {
		r("#now 2007-01-01 12:00:00");
		r("#nextStep A B");
		r("#nextStep B C");
		r("#nextStep C A");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:A [=> B => C => A!] <= C <= B <= [Broke cycle]");
		w("> p:B (next step: p:C)");
		w("> p:C (next step: p:A)");
	}

	@Test
	public void showMultipleParentsInCycle() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep A B");
		r("#nextStep B C");
		r("#nextStep C B");
		r("#trip B");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:B [=> C => B!]");
		w("  <= A");
		w("  <= C <= [Broke cycle]");
		w("> p:A (next step: p:B)");
		w("> p:C (next step: p:B)");
	}

	@Test
	public void indicateCycleClearlyWithGoal() {
		r("#now 2007-01-01 12:00:00");
		r("#startGoal A");
		r("#nextStep A B");
		r("#nextStep B C");
		r("#nextStep C A");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! A [=> B => C => A!] <= C <= B <= [Broke cycle]");
		w("> p:B (next step: p:C)");
		w("> p:C (next step: A)");
	}

	@Test
	public void priorStepsWorkWithCycles() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo B C");
		r("#priorTo C A");
		nowIs("2007-01-01 12:10:00");

		runStatusWithParents();
		w("! p:A [-> B -> C -> A!] <- C <- B <- [Broke cycle]");
		w("> p:B (first: p:C until 2007-01-02 12:00:00)");
		w("> p:C (first: p:A until 2007-01-02 12:00:00)");
	}

	@Test
	public void showNoCycleIfOneIsInactionable() {
		r("#now 2007-01-01 12:00:00");
		r("#priorTo A B");
		r("#priorTo B C");
		r("#priorTo C A");
		r("#wait A 1d");

		nowIs("2007-01-01 12:00:01");

		runStatus("-actionItemsFirst", "-withParents");
		w("! p:C [-> A -> B -> C!] <- B");
	}
}

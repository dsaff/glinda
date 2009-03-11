/**
 * 
 */
package test.net.saff.glinda.user.a_projects;

import net.saff.glinda.projects.core.Project;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class C_Pause extends CondCorrespondentOnDiskTest {
	@Test public void pauseProjects() {
		addImport(Project.class, "pause");
		r("#startProject Growth");
		r("#pause Growth");

		run("status");
		w("status:");
		w("z p:Growth (paused)");
	}

	@Test public void endOnResume() {
		addImport(Project.class, "pause");
		addImport(Project.class, "resume");
		r("#startProject Growth");
		r("#pause Growth");
		r("#resume Growth");

		run("status");
		w("status:");
		w("! p:Growth");
	}

	@Test public void preventChildrenFromHoldingUpSoftParents() {
		addImport(Project.class, "pause");
		r("#now 2007-01-01 12:00:00");
		r("#startProject FixFloor");
		r("#priorTo FixFloor CleanFloor");
		r("#pause CleanFloor");
		nowIs("2007-01-01 12:00:00");

		run("status", "-actionItemsFirst");
		w("status:");
		w("! p:FixFloor [-> CleanFloor]");
		w("z p:CleanFloor (paused)");
	}
}
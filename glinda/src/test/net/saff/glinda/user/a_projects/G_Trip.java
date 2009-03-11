package test.net.saff.glinda.user.a_projects;

import net.saff.glinda.projects.core.Project;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class G_Trip extends CondCorrespondentOnDiskTest {
	@Test public void showAllParentsIncludingTripDeep() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep B A");
		r("#nextStep C A");
		r("#trip A");
		r("#trip B");
		r("#nextStep D B");
		r("#nextStep E B");
		r("#trip D");
		r("#nextStep F D");
		r("#nextStep G D");
		
		nowIs("2007-01-01 12:00:01");
		
		run("status", "-actionItemsFirst", "-withParents");
		w("status:");
		w("! p:A");
		w("  <= B");
		w("    <= D");
		w("      <= F");
		w("      <= G");
		w("    <= E");
		w("  <= C");
	}
	

	@Test public void stayShallowUnlessParentIsTrip() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep B A");
		r("#nextStep C A");
		r("#trip A");
		r("#nextStep D B");
		r("#nextStep E B");
		r("#nextStep F D");
		r("#nextStep G D");
		
		nowIs("2007-01-01 12:00:01");
		
		run("status", "-actionItemsFirst", "-withParents");
		w("status:");
		w("! p:A");
		w("  <= B <=* D <=* F");
		w("  <= C");
	}
	
	@Test public void showTripsDifferently() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep B A");
		r("#nextStep C A");
		r("#trip A");
		r("#trip B");
		r("#nextStep D B");
		r("#nextStep E B");
		r("#trip D");
		r("#nextStep F D");
		r("#nextStep G D");
		
		nowIs("2007-01-01 12:00:01");
		
		run("status", "-actionItemsFirst", "-withParents", "-distinguishTrips");
		w("status:");
		w("! t:A");
		w("  <= B");
		w("    <= D");
		w("      <= F");
		w("      <= G");
		w("    <= E");
		w("  <= C");
	}
	
	@Test public void tripIsNextStepIfLastRequirement() {
		addImport(Project.class, "trip");
		r("#now 2007-01-01 12:00:00");
		r("#nextStep A B");
		r("#nextStep A C");
		r("#trip C");
		nowIs("2007-01-01 12:00:01");
		
		run("status", "-actionItemsFirst", "-withParents", "-distinguishTrips");
		w("status:");
		w("! p:B");
		w("! t:C");
		w("  <= A");
	}
}

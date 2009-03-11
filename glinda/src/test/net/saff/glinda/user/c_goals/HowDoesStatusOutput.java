/**
 * 
 */
package test.net.saff.glinda.user.c_goals;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.goals.Goal;

import org.junit.Test;
import org.junit.Test.None;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;


public class HowDoesStatusOutput extends CondCorrespondentOnDiskTest {
	@Test
	public void reportResumedGoals() {
		addImport(Project.class, "pause");
		addImport(Project.class, "resume");
		r("#NOW 01-01 12:00");
		r("#track MessyRooms: 1");
		r("#pause MessyRooms");
		r("#resume MessyRooms");
		nowIs("01-01 12:01");
		
		run("status");
		w("status:");
		w("> MessyRooms (was null, is 1)");
	}

	@Test
	public void readWithTimesFlag() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms: 1");
		r("#NOW 01-02 12:00");
		r("#track MessyRooms: 0");
		nowIs("01-02 12:01");

		run("status", "-withTimes");
		w("status:");
		w("> MessyRooms (was 1 at 01-01 12:00, is 0 at 12:00, next track 01-03 11:00)");
	}

	@Test
	public void displayMultipleGoals() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms 1");
		r("#track DishLoads 2");
		nowIs("01-01 12:01");

		run("status");
		w("status:");
		w("> MessyRooms (was null, is 1)");
		w("> DishLoads (was null, is 2)");
	}

	@Test(expected = None.class)
	public void automaticallySupplyRealNow() {
		r("#NOW 01-01 12:00");
		r("#track MessyRooms 3");
		run("status");
		ignoreOutput();
	}

	@Test
	public void handleEdits() {
		r("#NOW 07-01 12:00");
		r("#track MessyRooms 3");
		r("#edit MessyRooms CleanRooms");
		nowIs("07-05 12:00");

		run("status");
		w("status:");
		w("! CleanRooms (4 days since last track)");
	}
	
	@Test
	public void reportProjects() {
		r("#NOW 01-02 12:00");
		r("#track MessyRooms: 1");
		r("#startProject PaintTheWalls");
		nowIs("01-02 12:01");

		run("status");
		w("status:");
		w("> MessyRooms (was null, is 1)");
		w("! p:PaintTheWalls");
	}
	
	@Test public void complainIfGoalDoesntExist() {
		addImport(Goal.class, "set");
		r("#set ThisDoesntExist compareVelocity=true");
		try {
			run("status");
		} catch (Throwable e) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(out));
			assertThat(out.toString(),
					containsString("ThisDoesntExist is not a goal"));
		}
	}
}
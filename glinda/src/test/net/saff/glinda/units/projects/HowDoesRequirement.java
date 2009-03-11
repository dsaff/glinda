package test.net.saff.glinda.units.projects;

import java.util.Date;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.tasks.Task;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

@RunWith(Theories.class)
public class HowDoesRequirement extends LoqBookDataPoints {
	@Theory
	@Test(expected = NullPointerException.class)
	public void gripeAboutNullCreationTime(Project project) {
		Requirement.hardForever(null, project, null);
	}

	@Test(expected = NullPointerException.class)
	public void complainAboutNullType() {
		new Requirement<Project>(null, null, Task.withoutListener("A"), TimeInterval
				.everFrom(TimePoint.from(new Date())));
	}
}

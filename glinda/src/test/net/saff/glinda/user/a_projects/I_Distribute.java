package test.net.saff.glinda.user.a_projects;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class I_Distribute extends CondCorrespondentOnDiskTest {
  @Test
  public void a_worksInBasicCase() {
    r("#now 01-01 12:00");
    r("#priorTo A Spike");
    r("#priorTo B Spike");
    r("#distribute Spike");
    nowIs("01-01 12:00");

    run("status", "-actionItemsFirst", "-withParents");
    w("status:");
    w("! p:Spike_A <- A");
    w("! p:Spike_B <- B");
  }

  @RunWith(Theories.class)
  public static class DistributeUnits extends LoqBookDataPoints {
    @Theory
    public void useOldChildNameInNewChild(BracketedString parentName,
        BracketedString childName, GlindaTimePoint now) {
      GoingConcerns concerns = GoingConcerns.withDefaults();
      Project parent = concerns.startProject(parentName);
      Project child = concerns.startProject(childName);
      RequirementGraph<Project> graph = new RequirementGraph<Project>();
      graph.priorTo(parent, child, now, null);
      concerns.distribute(child, now, graph);
      assertThat(parent.getRequirements(graph).toString(),
          containsString(childName + "_" + parentName));
    }
  }
}

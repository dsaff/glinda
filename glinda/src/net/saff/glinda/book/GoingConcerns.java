/**
 * 
 */
package net.saff.glinda.book;

import static net.saff.glinda.book.ProjectContainerList.fresh;

import com.domainlanguage.time.TimeInterval;

import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.names.DoneListener;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.core.ProjectContainer;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.projects.goals.GoalTrackingLog;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.RequirementType;
import net.saff.glinda.projects.requirement.SupersmashStrategy;
import net.saff.glinda.projects.routines.Routine;
import net.saff.glinda.projects.routines.RoutineSpecification;
import net.saff.glinda.projects.tasks.Task;
import net.saff.glinda.time.GlindaTimePoint;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GoingConcerns implements DoneListener, Serializable {
  private final GoalTrackingLog goals;
  private ProjectContainer<Task> tasks = new TaskContainer();
  private ProjectContainer<Routine> routines = new RoutineContainer();

  private final ProjectContainerList containers;

  private GoingConcerns(SupersmashStrategy strategy) {
    goals = new GoalTrackingLog(strategy);
    containers = defaultContainers();
  }

  public static GoingConcerns withDefaults() {
    return withSupersmashStrategy(new IterateSupersmashStrategy());
  }

  public static GoingConcerns withSupersmashStrategy(
      SupersmashStrategy supersmashStrategy) {
    return new GoingConcerns(supersmashStrategy);
  }

  void nextStep(BracketedString projectName, BracketedString nextStep,
      GlindaTimePoint now, RequirementGraph<Project> graph) {
    Project project = startIfNeeded(projectName);
    project.addLink(Requirement.hardForever(project, startIfNeeded(nextStep),
        now), graph);
  }

  private ProjectContainerList defaultContainers() {
    return fresh().with(routines).with(goals).withDefault(tasks);
  }

  public Project startIfNeeded(BracketedString nextStep) {
    return containers.startIfNeeded(nextStep);
  }

  public boolean noSuchConcern(BracketedString nextStep) {
    return containers.noSuchConcern(nextStep);
  }

  public Project startProject(BracketedString nextStep) {
    Task created = tasks.create(nextStep);
    tasks.put(nextStep, created);
    return created;
  }

  public void done(BracketedString name, GlindaTimePoint now,
      RequirementGraph<Project> graph) {
    // TODO: pass-through
    containers.done(name, now, graph);
  }

  public Project findConcern(BracketedString name) {
    return containers.findConcern(name);
  }

  public List<String> statusLines(StatusRequest context) {
    return new StatusRequestor(context).status(concerns().values());
  }

  public Map<BracketedString, Project> concerns() {
    return containers.concernMap();
  }

  public Goal startGoal(BracketedString goalName) {
    return goals.getOrCreate(goalName);
  }

  Goal getGoal(BracketedString bracketedString) {
    return goals.getGoal(bracketedString);
  }

  public Goal getOrCreateGoal(BracketedString goalName) {
    return goals.getOrCreate(goalName.canonicalize());
  }

  public void edit(BracketedString originalName, BracketedString newName)
      throws LoqCommandExecutionException {
    if (!noSuchConcern(newName))
      throw new LoqCommandExecutionException("This name already exists: "
          + newName);
    findConcern(originalName).renameTo(newName);
  }

  public List<BracketedString> goalNames() {
    return goals();
  }

  public List<BracketedString> goals() {
    return goals.goalNames();
  }

  public void endRoutine(BracketedString routine) {
    routines.remove(routine);
  }

  public void startRoutine(RoutineSpecification spec)
      throws LoqCommandExecutionException {
    if (!noSuchConcern(spec.getRoutineName()))
      throw new LoqCommandExecutionException("Already a concern of that name");

    routines.put(spec.getRoutineName(), new Routine(spec, routines));
  }

  public void distribute(Project project, GlindaTimePoint now,
      RequirementGraph<Project> graph) {
    // TODO (May 21, 2008 1:21:25 PM): envious of graph?
    TimeInterval timeInterval = graph.linkDuration(now, null);
    for (Project each : project.getBlocked(graph))
      // TODO: test in that this is startIfNeeded, not startProject
      each.addLink(new Requirement<Project>(each, RequirementType.SOFT, this
          .startIfNeeded(new BracketedString(project + "_" + each)),
          timeInterval), graph);
    this.done(project.getNameWithoutPrefix(), GlindaTimePoint.from(timeInterval
        .start()), graph);
  }
}

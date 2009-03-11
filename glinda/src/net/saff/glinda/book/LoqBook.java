package net.saff.glinda.book;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.saff.glinda.ideas.Idea;
import net.saff.glinda.ideas.IdeaList;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.search.SearchCriteria;
import net.saff.glinda.interpretation.declaring.PropertyAssignment;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.interpretation.invoking.Delegate;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.core.DescendantAncestry;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.display.StatusRequest;
import net.saff.glinda.projects.display.StatusRequestor;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.routines.RoutineSpecification;
import net.saff.glinda.projects.routines.RoutineSpecificationParser;
import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeInterval;

public class LoqBook implements TypeSpecificParserFactory, Serializable {
	/**
   * 
   */
	private static final long serialVersionUID = 1L;

	public static LoqBook withDefaults() {
		return withConcerns(GoingConcerns.withDefaults());
	}

	public static LoqBook withConcerns(GoingConcerns concerns) {
		return new LoqBook(concerns);
	}

	private final GoingConcerns concerns;
	private GlindaTimePoint latestNow;
	private GlindaTimePoint realNow;
	private IdeaList ideas = new IdeaList();
	private Correspondent correspondent;
	private boolean clearRemovesDoneChildren = false;
	private Project pretenseReason = null;
	private WaitTime pretenseTime = null;
	private final RequirementGraph<Project> graph = new RequirementGraph<Project>();

	private LoqBook(GoingConcerns concerns) {
		this.concerns = concerns;
	}

	public List<BracketedString> goals() {
		return getConcerns().goals();
	}

	public void now(GlindaTimePoint now) {
		setLatestNow(now);
	}

	public void setNow(GlindaTimePoint glindaTimePoint) {
		realNow = glindaTimePoint;
	}

	public void done(BracketedString name) {
		name.markDoneIn(getLatestNow(), graph, getConcerns(), ideas);
	}

	public void clear(Project project) {
		project.clear(getLatestNow(), clearRemovesDoneChildren, graph);
	}

	public List<String> bucketize() {
		return ideas.bucketize(correspondent);
	}

	public List<String> status(EnumSet<Flag> flags) {
		List<String> status = getConcerns().statusLines(context(flags));
		addPretense(status);
		return status;
	}

	public void addPretense(List<String> status) {
		if (pretenseReason != null) {
			String introLine = "[=== pretending for " + pretenseReason + ": "
					+ pretenseTime + " ===]";
			status.add(0, introLine);
		}
	}

	public void idea(String idea) {
		ideas.add(idea);
	}

	public List<String> ideas() {
		return ideas.asStringList();
	}

	public void nextStep(BracketedString projectName, BracketedString nextStep) {
		getConcerns().nextStep(projectName, nextStep, getLatestNow(), getGraph());
	}

	public void priorTo(Project project1, Project project2,
			Duration untilExpiration) {
		// TODO: too many params
		project1.addLink(new Requirement<Project>(project1,
				net.saff.glinda.projects.requirement.RequirementType.SOFT,
				project2, linkDuration(untilExpiration)), getGraph());
	}

	public TimeInterval linkDuration(Duration untilExpiration) {
		return getGraph().linkDuration(getLatestNow(), untilExpiration);
	}

	public List<String> interestize(String interestedParty) {
		return ideas.interestize(interestedParty, correspondent);
	}

	public void setCorrespondent(Correspondent correspondent) {
		this.correspondent = correspondent;
	}

	public List<String> estimatize(SearchCriteria searchCriteria) {
		return ideas.find(searchCriteria).estimatize(correspondent);
	}

	public Iterable<Idea> find(SearchCriteria searchCriteria) {
		return ideas.find(searchCriteria);
	}

	private GlindaTimePoint statusNow() {
		if (pretenseTime != null)
			return pretenseTime.timeToStopWaiting(getLatestNow());
		return realNow;
	}

	public void track(BracketedString goalName, double realValue) {
		getConcerns().getOrCreateGoal(goalName)
				.track(getLatestNow(), realValue);
	}

	public void setGlobal(PropertyAssignment assignment)
			throws LoqCommandExecutionException {
		assignment.apply(this);
	}

	public void setClearRemovesDoneChildren(boolean clearRemovesDoneChildren) {
		this.clearRemovesDoneChildren = clearRemovesDoneChildren;
	}

	public List<String> heapify() {
		return requestor().heapify(correspondent,
				getConcerns().concerns().values());
	}

	public String goalStatus(Project goal) {
		return requestor().statusLine(goal);
	}

	public StatusRequestor requestor() {
		return new StatusRequestor(StatusRequest.asOf(statusNow(), getGraph()).pretendingFor(pretenseReason).withFlags(Flag.NONE)
		.makeRequest());
	}

	public void wait(Project project, WaitTime waitTime) {
		project.setWaitTime(waitTime.timeToStopWaiting(getLatestNow()));
	}

	public void harden(Project project) {
		ancestry(project).harden(
				project,
				StatusRequest.asOf(getLatestNow(), graph).pretendingFor(pretenseReason).withFlags(Flag.NONE)
				.makeRequest());
	}

	public StatusRequest context(EnumSet<Flag> flag) {
		// TODO: pass-through
		return StatusRequest.asOf(statusNow(), graph).pretendingFor(pretenseReason).withFlags(flag)
				.makeRequest();
	}

	public void distribute(Project project) {
		getConcerns().distribute(project, getLatestNow(), graph);
	}

	private DescendantAncestry ancestry(Project project) {
		// TODO: encapsulate graph
		return DescendantAncestry.fromProject(project, getLatestNow(), graph);
	}

	public void pretendTime(Project why, WaitTime wtime) {
		this.pretenseReason = why;
		this.pretenseTime = wtime;
	}

	public void endPretend() {
		this.pretenseReason = null;
		this.pretenseTime = null;
	}

	@SuppressWarnings("unchecked")
	public static Set<Class<?>> recognizedClasses = new HashSet<Class<?>>(
			Arrays.asList(Idea.class, Goal.class, Project.class,
					PropertyAssignment.class, RoutineSpecification.class));

	public Object parse(String string, Type type) throws ParseException {
		return parserFor(type).parse(string);
	}

	public TypeSpecificParser parserFor(Type type) {
		if (type == Idea.class)
			return new TypeSpecificParser() {
				/**
       * 
       */
				private static final long serialVersionUID = 1L;

				public Object parse(String string) {
					return ideas.get(new BracketedString(string));
				}
			};
		if (type == Goal.class)
			return new TypeSpecificParser() {
				/**
       * 
       */
				private static final long serialVersionUID = 1L;

				public Object parse(String string) {
					return getConcerns().getGoal(new BracketedString(string));
				}
			};
		if (type == Project.class)
			return new TypeSpecificParser() {
				/**
       * 
       */
				private static final long serialVersionUID = 1L;

				public Object parse(String string) {
					return getConcerns().startIfNeeded(
							new BracketedString(string));
				}
			};
		if (type == PropertyAssignment.class)
			return new TypeSpecificParser() {
				/**
       * 
       */
				private static final long serialVersionUID = 1L;

				public Object parse(String string) {
					return PropertyAssignment.parse(LoqBook.this, string);
				}
			};
		if (type == RoutineSpecification.class)
			return new TypeSpecificParser() {
				/**
       * 
       */
				private static final long serialVersionUID = 1L;

				public Object parse(String string) {
					return new RoutineSpecificationParser().parseWithName(
							string, getLatestNow());
				}
			};
		if (type == RequirementGraph.class)
			return new TypeSpecificParser() {
				/**
       * 
       */
				private static final long serialVersionUID = 1L;

				public Object parse(String string) {
					return getGraph();
				}
			};
		return new StaticParser().parserFor(type);
	}

	@Delegate
	public GoingConcerns getConcerns() {
		return concerns;
	}

	public String goalStatus(String string) {
		return goalStatus(getConcerns()
				.findConcern(new BracketedString(string)));
	}

	private void setLatestNow(GlindaTimePoint latestNow) {
		this.latestNow = latestNow;
	}

	private GlindaTimePoint getLatestNow() {
		return latestNow;
	}

	public void setDefaultLinkExpiration(Duration expirationTime) {
		getGraph().setDefaultLifespan(expirationTime);
	}

	public Project startProject(BracketedString project1) {
		return getConcerns().startProject(project1);
	}

	public RequirementGraph<Project> getGraph() {
		return graph;
	}
}

// TODO (May 21, 2008 1:22:44 PM): complex
/**
 * 
 */
package net.saff.glinda.projects.display;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.saff.glinda.ideas.Heap;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.projects.core.DescendantAncestry;
import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.Cycle;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.requirement.Requirement;
import net.saff.glinda.projects.requirement.RequirementGraph;
import net.saff.glinda.projects.requirement.StatusInstant;
import net.saff.glinda.projects.status.Status;
import net.saff.glinda.projects.status.StatusDeterminant;
import net.saff.glinda.projects.status.StatusPrefix;
import net.saff.glinda.projects.tasks.ActivityRequestor;
import net.saff.glinda.projects.tasks.ChainStatus;

public class StatusRequestor {
	private final StatusRequest statusRequest;
	private int linesReturned = 0;

	public StatusRequestor(StatusRequest request) {
		this.statusRequest = request;
	}

	public String statusLine(Project project) {
		return getStatusRequest().format(nameForStatusLine(project),
				status(project));
	}

	public String nameForStatusLine(Project project) {
		return project.nameForStatusLine(getStatusRequest().getFlags());
	}

	public ArrayList<String> statusLinesStartingWith(String prefix,
			Collection<Project> projects) {
		ArrayList<String> statusLines = new ArrayList<String>();
		for (Project project : projects) {
			if (statusRequest.getFlags().contains(Flag.topTen)
					&& linesReturned >= 10)
				break;
			String statusLine = statusLine(project);
			if (statusLine.startsWith(prefix)) {
				linesReturned++;
				statusLines.add(statusLine);
			}
		}
		return statusLines;
	}

	public List<String> status(Collection<Project> projects) {
		ArrayList<String> statusLines = new ArrayList<String>();
		for (String each : prefixesInOrder())
			statusLines.addAll(statusLinesStartingWith(each, projects));
		return statusLines;
	}

	public List<String> prefixesInOrder() {
		if (!getStatusRequest().shouldShowActionItemsFirst())
			return asList("");

		ArrayList<String> prefixes = new ArrayList<String>();
		for (StatusPrefix statusPrefix : StatusPrefix.values())
			prefixes.add(statusPrefix.toString());
		return prefixes;
	}

	public Status<Project> status(Project project) {
		final StatusDeterminant<Project> determinant = firstApplicableStatus(project);
		Status<Project> status = determinant.status(getStatusRequest());
		// TODO: test in
		if (status == null)
			return new Status<Project>(StatusPrefix.NOT_OK) {
				@Override
				public String[] getComments(StatusInstant<Project> instant) {
					return new String[] { determinant + " returns null" };
				}
			};
		// validateStatus(status, project, determinant);
		return status;
	}

	public void validateStatus(Status<Project> status, Project project,
			StatusDeterminant<Project> determinant) {
		if (status == null)
			throw new IllegalStateException(determinant + " returns null for "
					+ project);
	}

	@SuppressWarnings("unchecked")
	public StatusDeterminant<Project> firstApplicableStatus(Project project) {
		List<StatusDeterminant<Project>> determinants = Arrays.asList(
				new PausedDeterminant(project.isPaused()), project
						.getWaitCompletion(), cycleDeterminant(project),
				blockedDeterminant(project), activeStatusDeterminant(project));
		for (StatusDeterminant<Project> each : determinants)
			if (each.canBeApplied(getStatusRequest())
					&& each.status(getStatusRequest()) != null)
				return each;
		return null;
	}

	public ProjectStatusDeterminant activeStatusDeterminant(
			final Project project) {
		return new ActiveStatusDeterminant(project);
	}

	public ProjectStatusDeterminant cycleDeterminant(final Project project) {
		return new ProjectStatusDeterminant() {
			/**
       * 
       */
			private static final long serialVersionUID = 1L;

			@Override
			public Status<Project> status(StatusInstant<Project> instant) {
				Cycle<Requirement<Project>> cycle = findCycle(instant);
				if (project.getRequirements(getGraph()).getContents().contains(
						oldestLink(cycle)))
					return new ChainStatus(project, Project.LINK_FINDER);
				return new ActivityRequestor(getStatusRequest())
						.blockedStatus(project);
			}

			private RequirementGraph<Project> getGraph() {
				return statusRequest.getGraph();
			}

			public Cycle<Requirement<Project>> findCycle(
					StatusInstant<Project> instant) {
				for (Requirement<Project> each : getGraph()
						.getChildren(project)) {
					Cycle<Requirement<Project>> cycle = new DescendantAncestry(
							instant.getNow(), each, Project.LINK_FINDER)
							.findCycle(instant);
					if (cycle != null)
						return cycle;
				}
				return null;
			}

			private Requirement<?> oldestLink(Cycle<Requirement<Project>> cycle) {
				return Collections.min(cycle, new Comparator<Requirement<?>>() {
					public int compare(Requirement<?> o1, Requirement<?> o2) {
						return ((Integer) o1.serialNumber()).compareTo(o2
								.serialNumber());
					}
				});
			}

			@Override
			public boolean canBeApplied(StatusInstant<Project> instant) {
				return findCycle(instant) != null;
			}
		};
	}

	public BlockedDeterminant blockedDeterminant(final Project project) {
		return new BlockedDeterminant(project);
	}

	public StatusRequest getStatusRequest() {
		return statusRequest;
	}

	public List<String> heapify(final Correspondent correspondent,
			Collection<Project> projects) {
		final ArrayList<String> answers = new ArrayList<String>();

		Heap<Project> heap = new Heap<Project>(10, new Comparator<Project>() {
			public int compare(Project o1, Project o2) {
				String choice1 = o1.heapifyChoice(1);
				String choice2 = o2.heapifyChoice(2);
				String answer = correspondent.getAnswer(
						"Which should come first?", choice1, choice2);
				if (answer == null)
					throw new IllegalArgumentException(
							"Correspondent returned null asking about " + o1
									+ " and " + o2);
				if (answer.endsWith(choice1)) {
					answers.add(priorTo(o1, o2));
					return -1;
				} else {
					answers.add(priorTo(o2, o1));
					return 1;
				}
			}

			private String priorTo(Project o1, Project o2) {
				return String.format("#priorTo %s %s", o2, o1);
			}
		});

		for (Project project : projects)
			if (status(project).hasPrefix(StatusPrefix.NOT_OK))
				heap.insert(project);

		return answers;
	}
}

package test.net.saff.glinda.user.h_ideas;

import net.saff.glinda.ideas.Idea;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class HowDoesBucketizeOutput_WIthAnyCorrespondent extends CondCorrespondentOnDiskTest {
	@Test public void handleDifferentAnswers() {
		r("#idea Nina");
		r("#idea Pinta");
		r("#idea Santa Maria");
		r("#idea Mayflower");

		ifQuestionContains("Nina").thenChooseAnswerContaining("day");
		ifQuestionContains("Pinta").thenChooseAnswerContaining("week");
		ifQuestionContains("Santa").thenChooseAnswerContaining("month");
		ifQuestionContains("May").thenChooseAnswerContaining("year");

		run("bucketize");
		w("bucketize:");
		w("#bucket Nina day");
		w("#bucket Pinta week");
		w("#bucket [Santa Maria] month");
		w("#bucket Mayflower year");
	}

	@Test public void ignorePreviouslyBucketizedIdeas() {
		addImport(Idea.class, "bucket");
		r("#idea Be happy");
		r("#bucket [Be happy] month");
		run("bucketize");
		w("bucketize:");
	}
}

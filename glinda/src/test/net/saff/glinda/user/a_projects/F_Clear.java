/**
 * 
 */
package test.net.saff.glinda.user.a_projects;

import static com.domainlanguage.time.Duration.seconds;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.hasItem;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.time.GlindaTimePoint;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;
import test.net.saff.glinda.testclasstypes.LoqBookDataPoints;

public class F_Clear extends CondCorrespondentOnDiskTest {
	@Test
	public void makeProjectsActionable() {
		r("#nextStep Shrinkage Growage");
		r("#clear Shrinkage");

		run("status");
		w("status:");
		w("! p:Shrinkage");
		w("! p:Growage");
	}

	@Test
	public void onlyRemoveOneLink() {
		r("#nextStep Shrinkage Growage");
		r("#nextStep Shrinkage Runnage");
		r("#clear Shrinkage");

		run("status");
		w("status:");
		w("> p:Shrinkage (next step: p:Runnage)");
		w("! p:Growage");
		w("! p:Runnage");
	}

	@Test
	public void removeUpToFirstNotDoneLink() {
		r("#now 2007-01-01 12:00:00");
		r("#setGlobal clearRemovesDoneChildren=true");
		r("#nextStep Shrinkage Growage");
		r("#nextStep Shrinkage Runnage");
		r("#done Growage");
		r("#clear Shrinkage");
		nowIs("2007-01-01 12:00:01");

		run("status");
		w("status:");
		w("! p:Shrinkage");
		w("! p:Runnage");
	}

	@RunWith(Theories.class)
	public static class DeepUnit extends LoqBookDataPoints {
		@Theory
		public void removeUpToFirstNotDoneLinkDeepUnit(GlindaTimePoint now) {
			LoqBook book = LoqBook.withDefaults();
			book.now(now);
			book.setClearRemovesDoneChildren(true);
			book.nextStep(new BracketedString("Shrinkage"),
					new BracketedString("Growage"));
			book.nextStep(new BracketedString("Shrinkage"),
					new BracketedString("Runnage"));
			book.done(new BracketedString("Growage"));
			book.clear(book.getConcerns().findConcern(new BracketedString("Shrinkage")));
			book.setNow(now.plus(seconds(1)));

			assertThat(book.status(Flag.NONE),
					hasItem(containsString("! p:Shrinkage")));
		}
	}

	@Test
	public void notBadWithZeroLinks() {
		r("#startProject Shrinkage");
		r("#clear Shrinkage");

		run("status");
		w("status:");
		w("! p:Shrinkage");
	}

	@Test
	public void releaseDependentProjects() {
		r("#now 1998-01-03 12:00:00");
		r("#startProject HangPicture");
		r("#nextStep HangPicture [find a nail]");
		r("#done [find a nail]");

		run("status");
		w("status:");
		w("! p:HangPicture");
	}

	@Test
	public void removeParentFromPicture() {
		r("#now 1998-01-03 12:00:00");
		r("#startProject HangPicture");
		r("#nextStep [find a nail] HangPicture");
		r("#done [find a nail]");

		run("status", "-withParents");
		w("status:");
		w("! p:HangPicture");
	}

	@Test
	public void removeParentFromMulitpleChildren() {
		r("#now 1998-01-03 12:00:00");
		r("#nextStep FindANail HangPicture");
		r("#nextStep FindANail BeGoofy");
		r("#done FindANail");

		run("status", "-withParents");
		w("status:");
		w("! p:HangPicture");
		w("! p:BeGoofy");
	}
}
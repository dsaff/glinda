package test.net.saff.glinda.units.ideas;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import net.saff.glinda.ideas.correspondent.UserKeyboardCorrespondent;

import org.junit.Before;
import org.junit.Test;

import test.net.saff.glinda.HumanUser;

public class TestUserKeystrokeCollector {
	@Before
	public void ensureUserAvailable() {
		assumeTrue(HumanUser.isAvailable());
	}

	@Test
	public void works() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"What year was the Declaration of Independence signed?",
				"a) 1775", "b) 1776", "c) 1777"), is("b) 1776"));
	}

	@Test
	public void triangulate() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"What year was the Declaration of Independence signed?",
				"a) 1776", "b) 1777", "c) 1778"), is("a) 1776"));
	}

	@Test
	public void differentLetters() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"What year was the Declaration of Independence signed?",
				"d) 1776", "e) 1777", "f) 1778"), is("d) 1776"));
	}

	@Test
	public void fourChoices() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"What year was the Declaration of Independence signed?",
				"d) 1772", "e) 1773", "f) 1774", "g) 1775", "h) 1776"),
				is("h) 1776"));
	}

	@Test
	public void keyBoardChoices() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"Can you press the 'y' key to make this go away?", "y) yes",
				"n) No, I had to use the mouse"), is("y) yes"));
	}

	@Test
	public void putsOptionsInRightOrder() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"Please click the first button", "1) one", "2) two"),
				is("1) one"));
	}

	@Test
	public void putOptionsVertically() {
		assertThat(new UserKeyboardCorrespondent().getAnswer(
				"Are these arranged vertically?", "y) yes", "n) no"),
				is("y) yes"));
	}
}

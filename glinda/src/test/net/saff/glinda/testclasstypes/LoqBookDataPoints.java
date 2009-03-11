package test.net.saff.glinda.testclasstypes;

import java.util.EnumSet;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.JournalParser;
import net.saff.glinda.journal.ObjectJournalParser;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.goals.Goal;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.routines.RelativeTimeRoutineSpecification;
import net.saff.glinda.projects.routines.RoutineSpecification;
import net.saff.glinda.projects.routines.RoutineSpecificationParser;
import net.saff.glinda.time.DayOfWeek;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;

import com.domainlanguage.time.Duration;

public class LoqBookDataPoints {
	@DataPoints
	public static String[] strings = { "MessyRooms", "DishLoads",
			"ToadsInGarden", "Frogs" };

	@DataPoints
	public static BracketedString[] bstrings = { new BracketedString("Cars"),
			new BracketedString("Trucks"), new BracketedString("Motorcycles") };

	@DataPoint
	public static GlindaTimePoint jan1 = new GlindaTimePointParser()
			.parse("2007-01-01 12:00:00");
	@DataPoint
	public static GlindaTimePoint jan2 = new GlindaTimePointParser()
			.parse("2007-01-02 12:00:00");
	@DataPoint
	public static GlindaTimePoint now = GlindaTimePoint.now();

	@DataPoints
	public static int[] ints = { 1, 2, 7, 123456 };

	@DataPoints
	public static double[] doubles = { 1.5, 47 };

	@DataPoint
	public static JournalParser<Object> blank() {
		return new ObjectJournalParser(true);
	}

	@DataPoint
	public static JournalParser<Object> throwsExceptionOnProblem() {
		return new ObjectJournalParser(false);
	}

	@DataPoint
	public static JournalParser<Object> oneGoal()
			throws LoqCommandExecutionException {
		JournalParser<Object> parser = new ObjectJournalParser(true);
		parser.a("#startGoal AProject");
		return parser;
	}

	@DataPoint
	public static LoqBook blankBook() {
		return LoqBook.withDefaults();
	}

	@DataPoint
	public static LoqBook bookWithOneRoutine()
			throws LoqCommandExecutionException {
		LoqBook book = LoqBook.withDefaults();
		book.getConcerns().startRoutine(
				new RoutineSpecificationParser().parse2(new BracketedString(
						"ARoutine"), "each day at 9pm", GlindaTimePoint.now()));
		return book;
	}

	@DataPoint
	public static final EnumSet<Flag> someOtherFlag = EnumSet
			.of(Flag.weirdFlag);

	@DataPoint
	public static final EnumSet<Flag> actionFirstFlag = EnumSet
			.of(Flag.actionItemsFirst);

	@DataPoints
	public static boolean[] booleans = { true };

	@DataPoints
	public static Duration[] durations = { Duration.days(0),
			Duration.milliseconds(100L), Duration.days(2) };

	@DataPoint
	public static Goal freshGoal() {
		return goalWithoutListener("Frogs");
	}

	@DataPoint
	public static Goal pausedGoal() {
		Goal status = goalWithoutListener("Chickens");
		status.pause();
		return status;
	}

	@DataPoint
	public static Goal velocityGoal() {
		Goal status = goalWithoutListener("Cows");
		status.getSettings().setCompareVelocity(true);
		return status;
	}

	@DataPoint
	public static Goal moreIsBetterGoal() {
		Goal status = goalWithoutListener("MoreCows");
		status.getSettings().setMoreIsBetter(true);
		return status;
	}

	private static Goal goalWithoutListener(String name) {
		return new Goal(new BracketedString(name), null,
				new IterateSupersmashStrategy());
	}

	@DataPoint
	public static RoutineSpecification spec = new RelativeTimeRoutineSpecification(
			new BracketedString("Somewhat"), "9pm");

	@DataPoints
	public static DayOfWeek[] dows = DayOfWeek.values();
}

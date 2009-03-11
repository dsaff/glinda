package test.net.saff.glinda.testclasstypes;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.util.LinkedList;

import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.core.GlindaInvocation;
import net.saff.glinda.ideas.Idea;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.journal.FreedomFile;
import net.saff.glinda.journal.JournalFile;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;

import org.junit.Before;
import org.junit.internal.AssumptionViolatedException;

public abstract class OnDiskLoqBookTest {
	private StringWriter writer = new StringWriter();
	private GlindaTimePoint now;
	private LinkedList<String> output = new LinkedList<String>();

	protected abstract Correspondent getCorrespondent();

	@Before
	public void setWriter() {
		addImport(Idea.class, "interest");
	}

	protected void addImport(Class<?> type, String methodName) {
		r("##import " + type.getName() + "." + methodName);
	}

	protected void r(String line) {
		writer.append(line + "\n");
	}

	protected String output(GoingConcerns concerns, String... command) {
		try {
			writer.close();
			return new GlindaInvocation(new JournalFile(FreedomFile
					.fromString(writer.toString()), false), getCorrespondent(),
					concerns, command).output(now);
		} catch (AssumptionViolatedException e) {
			throw e;
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	protected void nowIs(String string) {
		now = new GlindaTimePointParser().parse(string);
	}

	protected void run(String... command) {
		String[] outputLines = output(GoingConcerns.withDefaults(), command).split("\n");
		output = new LinkedList<String>(asList(outputLines));
	}

	protected void w(String line) {
		assertThat(output.poll(), is(line));
	}

	protected void done() {
		w(null);
	}

	protected void ignoreOutput() {
		output.clear();
	}
}
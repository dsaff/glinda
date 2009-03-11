package net.saff.glinda.core;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import javax.net.ServerSocketFactory;

import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.ideas.correspondent.Correspondent;
import net.saff.glinda.ideas.correspondent.UserKeyboardCorrespondent;
import net.saff.glinda.interpretation.finding.CommandStrings;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.journal.FreedomFile;
import net.saff.glinda.journal.JournalFile;
import net.saff.glinda.journal.JournalParser;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;
import net.saff.glinda.projects.requirement.SupersmashStrategy;
import net.saff.glinda.time.GlindaTimePoint;

public class GlindaInvocation {
	public static GlindaInvocation fromLocation(String location,
			String... commands) {
		return fromLocationAndStrategy(location,
				new IterateSupersmashStrategy(), commands);
	}

	public static GlindaInvocation fromLocationAndStrategy(String location,
			SupersmashStrategy supersmashStrategy, String... commands) {
		// TODO: use builder
		return new GlindaInvocation(location, supersmashStrategy, commands);
	}

	private final Correspondent correspondent;
	private String[] commands;
	private final JournalFile journal;
	private final GoingConcerns concerns;

	public GlindaInvocation(JournalFile journal, Correspondent correspondent,
			GoingConcerns concerns, String... commands) {
		this.journal = journal;
		this.correspondent = correspondent;
		this.commands = commands;
		this.concerns = concerns;
	}

	public GlindaInvocation(JournalFile journal, SupersmashStrategy strategy,
			String... commands) {
		this(journal, new UserKeyboardCorrespondent(), GoingConcerns
				.withSupersmashStrategy(strategy), commands);
	}

	private GlindaInvocation(String location, SupersmashStrategy strategy,
			String... commands) {
		this(FreedomFile.fromFile(location), strategy, commands);
	}

	public GlindaInvocation(FreedomFile source, SupersmashStrategy strategy,
			String... commands) {
		this(new JournalFile(source, true), strategy, commands);
	}

	public Callable<String> topLevelCallable(final GlindaTimePoint now) {
		if (isServerCommand())
			return new Callable<String>() {
				public String call() throws Exception {
					return runServer();
				}
			};
		if (isClientCommand())
			return new Callable<String>() {
				public String call() throws Exception {
					return runClient(strings());
				}
			};
		return new LocalTopLevelCallable(now, this);
	}

	public String output(GlindaTimePoint now) throws Exception {
		return topLevelCallable(now).call();
	}

	public boolean isClientCommand() {
		return strings().getFirst().equals("client");
	}

	public boolean isServerCommand() {
		return strings().getFirst().equals("server");
	}

	public String runCommand(GlindaTimePoint now)
			throws LoqCommandExecutionException {
		return readParser().invoke(now, getCorrespondent(), strings());
	}

	public CommandStrings strings() {
		return new CommandStrings(commands);
	}

	public JournalParser<LoqBook> readParser()
			throws LoqCommandExecutionException {
		return getJournal().readParser(concerns);
	}

	public String runClient(CommandStrings strings)
			throws LoqCommandExecutionException {
		return port().runClient(strings.getRest().getRest());
	}

	public String runServer() {
		port().createServer(getJournal()).run();
		return "server started";
	}

	private Port port() {
		return Port.fromPortNumber(ServerSocketFactory.getDefault(), Integer
				.valueOf(commands[1]));
	}

	public String output() throws Exception {
		return output(GlindaTimePoint.now());
	}

	public void printOutput(PrintStream printStream) {
		CommandOutputStream cstream = new CommandOutputStream(printStream);
		try {
			cstream.print(output());
		} catch (Exception e) {
			e.printStackTrace(printStream);
		}
	}

	public Correspondent getCorrespondent() {
		return correspondent;
	}

	public JournalFile getJournal() {
		return journal;
	}
}

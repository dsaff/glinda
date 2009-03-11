/**
 * 
 */
package net.saff.glinda.journal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.MalformedInputException;

import net.saff.glinda.book.GoingConcerns;
import net.saff.glinda.book.LoqBook;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;

public class JournalFile implements Serializable {
	// TODO: figure out indentation
	private static final long serialVersionUID = 1L;
	protected JournalParser<LoqBook> parser;
	private final FreedomFile source;
	private final boolean ignoreProblems;

	// TODO: use factory methods
	public JournalFile(FreedomFile source, boolean ignoreProblems) {
		this.source = source;
		this.ignoreProblems = ignoreProblems;
	}

	public JournalFile(String fileName, boolean ignoreProblems) {
		this(FreedomFile.fromFile(fileName), ignoreProblems);
	}

	public void readBook(GoingConcerns concerns) throws IOException, LoqCommandExecutionException {
		BufferedReader reader = createReader(concerns);
		while (true) {
			String line = null;
			try {
				line = reader.readLine();
				if (line == null)
					break;
				parser.a(line);
			} catch (MalformedInputException e) {
				throw new RuntimeException("Malformed exception right after "
						+ line);
			}
		}
		reader.close();
	}

	private BufferedReader createReader(GoingConcerns concerns) throws IOException {
		BufferedReader reader = source.createReentrantReader();
		if (reader == null) {
			reader = source.createFreshReader();
			parser = resetParser();
			parser.setTarget(LoqBook.withConcerns(concerns));
		}
		return reader;
	}

	protected JournalParser<LoqBook> resetParser() {
		return new FastTimeParser(isIgnoreProblems());
	}

	public JournalParser<LoqBook> readParser(GoingConcerns concerns)
			throws LoqCommandExecutionException {
		try {
			readBook(concerns);
		} catch (IOException e) {
			throw new LoqCommandExecutionException(e);
		}
		// TODO: encapsulate parser
		return parser;
	}

	protected boolean isIgnoreProblems() {
		return ignoreProblems;
	}
}

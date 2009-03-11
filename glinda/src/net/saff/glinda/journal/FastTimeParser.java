/**
 * 
 */
package net.saff.glinda.journal;

import java.io.Serializable;

import net.saff.glinda.book.LoqBook;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;
import net.saff.glinda.time.GlindaTimePointParser;

// TODO: does this belong in this class?
class FastTimeParser extends JournalParser<LoqBook>
		implements Serializable {
	private static final long serialVersionUID = 1L;

	FastTimeParser(boolean ignoreProblems2) {
		super(ignoreProblems2);
	}

	@Override
	public void a(String line) throws LoqCommandExecutionException {
		// TODO: DUP
		if (line.startsWith("#NOW")) {
			getTarget().getTarget().now(
					new GlindaTimePointParser().parse(line.substring(5)));
			return;
		}
		super.a(line);
	}
}
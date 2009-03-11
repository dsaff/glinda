package net.saff.glinda.journal;



import net.saff.glinda.interpretation.finding.StringTarget;

public class ObjectJournalParser extends JournalParser<Object> {	
	public ObjectJournalParser(boolean ignoreProblems) {
		super(ignoreProblems);
	}

	// TODO (Apr 20, 2008 1:49:24 AM): are these used?

	public boolean isUseful(String command) {
		return !commandForLine(command).isUselessFor(getTarget());
	}

	public void setTarget(StringTarget<Object> target) {
		this.target = target;
	}
}
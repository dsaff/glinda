package net.saff.glinda.interpretation.finding;

import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;

public class NoSuchLoqMethodException extends LoqCommandExecutionException {
	public NoSuchLoqMethodException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}

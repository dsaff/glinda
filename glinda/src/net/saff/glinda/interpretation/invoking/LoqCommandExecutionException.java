package net.saff.glinda.interpretation.invoking;

import java.util.List;

public class LoqCommandExecutionException extends Exception {
	public LoqCommandExecutionException(Exception e) {
		super(e);
	}

	public LoqCommandExecutionException(Exception e, List<String> args) {
		super(String.format("On arguments %s", args), e);
	}

	public LoqCommandExecutionException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}

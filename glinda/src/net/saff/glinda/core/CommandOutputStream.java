package net.saff.glinda.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;

public class CommandOutputStream {
	private OutputStream out;

	public CommandOutputStream(OutputStream out) {
		this.out = out;
	}

	public OutputStream getOut() {
		return out;
	}

	void print(String output)
			throws LoqCommandExecutionException {
		OutputStreamWriter writer = new OutputStreamWriter(getOut());
	
		try {
			writer.append(output);
			writer.close();
		} catch (IOException e) {
			throw new LoqCommandExecutionException(e);
		}
	}
}
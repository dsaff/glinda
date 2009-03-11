package net.saff.glinda.journal;

import java.io.Serializable;

public interface LineInterpreter extends Serializable {
	public abstract boolean interpretsSuccessfully(String line)
			throws Exception;
}
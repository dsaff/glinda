package net.saff.glinda.ideas.correspondent;

import java.util.Arrays;

import net.saff.stubbedtheories.ConstructorEquality;

public class Question implements ConstructorEquality {
	private final String[] options;
	private final String prompt;

	public Question(String question, String... options) {
		this.prompt = question;
		this.options = options;
	}

	public String[] getOptions() {
		return options;
	}

	public String getPrompt() {
		return prompt;
	}

	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Question))
			return false;
		Question q = (Question) obj;
		return (q.prompt.equals(prompt) && Arrays.equals(q.options, options));
	}
}

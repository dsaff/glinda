package net.saff.glinda.interpretation.declaring;

import net.saff.glinda.interpretation.finding.UnspecifiedStringTarget;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;

public class PropertyAssignment {
	private final String name;
	private final String value;
	private final TypeSpecificParserFactory parser;

	public PropertyAssignment(TypeSpecificParserFactory parser, String name, String value) {
		this.parser = parser;
		this.name = name;
		this.value = value;
	}

	public void apply(Object target) throws LoqCommandExecutionException {
		Command.required(setterName(), "" + value).runOn(
				new UnspecifiedStringTarget(target, parser));
	}

	private String setterName() {
		return "set" + initialCaps();
	}

	String initialCaps() {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static PropertyAssignment parse(TypeSpecificParserFactory parser, String string) {
		String[] parts = string.split("=");
		return new PropertyAssignment(parser, parts[0], parts[1]);
	}
}

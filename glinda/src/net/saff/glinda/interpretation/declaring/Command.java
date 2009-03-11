package net.saff.glinda.interpretation.declaring;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.saff.glinda.interpretation.finding.CommandStrings;
import net.saff.glinda.interpretation.finding.Function;
import net.saff.glinda.interpretation.finding.NoSuchLoqMethodException;
import net.saff.glinda.interpretation.finding.StringTarget;
import net.saff.glinda.interpretation.invoking.GlindaMethod;
import net.saff.glinda.interpretation.invoking.LoqCommandExecutionException;

public class Command {
	private final CommandStrings command;
	private final List<GlindaMethod> imports;
	private final boolean ignoreProblems;

	private String cachedLowerCaseMethodName;

	public static Command required(String... command) {
		return new Command(false, Arrays.asList(command));
	}

	public static Command required(List<GlindaMethod> imports,
			List<String> command) {
		return new Command(false, imports, command);
	}

	// TODO: do I need all of these?
	public static Command required(List<GlindaMethod> imports,
			LinkedList<String> command) {
		return new Command(false, imports, command);
	}

	private Command(boolean ignoreProblems, List<String> list) {
		this(ignoreProblems, Collections.<GlindaMethod> emptyList(), list);
	}

	public Command(boolean ignoreProblems, List<GlindaMethod> imports,
			List<String> list) {
		this.ignoreProblems = ignoreProblems;
		this.imports = imports;
		this.command = new CommandStrings(list).compressBrackets();
	}

	public Object runOn(StringTarget<?> stringTarget)
			throws NoSuchLoqMethodException, LoqCommandExecutionException {
		return findFunction(stringTarget).invoke(argList().toList());
	}

	public Function findFunction(StringTarget<?> target) {
		// TODO: rip out method name and pass in for easier debugging
		// TODO: match Google indentation
		Function bestFunction = Function.useless(ignoreProblems, methodName());
		bestFunction = betterTargetMethod(target, bestFunction);
		bestFunction = betterImportMethod(target, bestFunction);
		return bestFunction;
	}

	private Function betterImportMethod(StringTarget<?> target,
			Function bestFunction) {
		for (GlindaMethod method : imports)
			if (nameMatches(method))
				return Function.delayedDelegate(target, method);
		return bestFunction;
	}

	private Function betterTargetMethod(StringTarget<?> target,
			Function bestFunction) {
		for (GlindaMethod method : target
				.availableMethods(lowerCaseMethodName()))
			if (canBeApplied(method)) {
				Function candidate = Function.of(target, method);
				if (firstIsBetterThanSecond(candidate, bestFunction))
					bestFunction = candidate;
			}
		return bestFunction;
	}

	private boolean canBeApplied(GlindaMethod method) {
		return nameMatches(method) && hasCompatibleParameterCount(method);
	}

	private boolean hasCompatibleParameterCount(GlindaMethod method) {
		return method.parameterCount() != 0 || desiredLength() == 0;
	}

	private boolean nameMatches(GlindaMethod method) {
		return method.nameMatches(lowerCaseMethodName());
	}

	private String lowerCaseMethodName() {
		if (cachedLowerCaseMethodName == null)
			cachedLowerCaseMethodName = methodName().toLowerCase();
		return cachedLowerCaseMethodName;
	}

	private boolean firstIsBetterThanSecond(Function candidate,
			Function bestFunction) {
		int candidateDistance = candidate.argLengthDifference(desiredLength());
		int bestDistance = bestFunction.argLengthDifference(desiredLength());
		return candidateDistance < bestDistance;
	}

	private int desiredLength() {
		return argList().size();
	}

	private String methodName() {
		return command.getFirst();
	}

	public CommandStrings argList() {
		return command.getRest();
	}

	private String trailingNewLines(Iterable<?> iterable) {
		StringBuffer buffer = new StringBuffer();

		for (Object object : iterable) {
			buffer.append(object);
			buffer.append("\n");
		}

		return buffer.toString();
	}

	public String output(StringTarget<?> stringTarget)
			throws LoqCommandExecutionException {
		return addNewlinesIfNeeded(runOn(stringTarget));
	}

	public String addNewlinesIfNeeded(Object result) {
		if (result instanceof Iterable)
			return methodName() + ":\n"
					+ trailingNewLines((Iterable<?>) result);
		return methodName() + ": " + result + "\n";
	}

	public boolean isUselessFor(StringTarget<?> stringTarget) {
		return findFunction(stringTarget).isUseless();
	}
}

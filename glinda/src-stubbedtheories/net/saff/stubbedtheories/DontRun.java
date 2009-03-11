package net.saff.stubbedtheories;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class DontRun extends Runner {
	public DontRun(@SuppressWarnings("unused") Class<?> type) {

	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription("should not run");
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(getDescription());
		notifier.fireTestFinished(getDescription());
	}
}

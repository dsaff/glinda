/**
 * 
 */
package net.saff.glinda.core;

import java.util.concurrent.Callable;

import net.saff.glinda.time.GlindaTimePoint;

public class LocalTopLevelCallable implements Callable<String> {
	private final GlindaTimePoint now;
	private final GlindaInvocation invocation;

	// TODO (Apr 20, 2008 1:43:19 AM): will be cycle

	public LocalTopLevelCallable(GlindaTimePoint now, GlindaInvocation invocation) {
		this.now = now;
		this.invocation = invocation;
	}

	public String call() throws Exception {
		return getInvocation().runCommand(getNow());
	}

	public GlindaTimePoint getNow() {
		return now;
	}

	public GlindaInvocation getInvocation() {
		return invocation;
	}
}
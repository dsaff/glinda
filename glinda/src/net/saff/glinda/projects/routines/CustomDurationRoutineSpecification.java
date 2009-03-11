/**
 * 
 */
package net.saff.glinda.projects.routines;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;

class CustomDurationRoutineSpecification extends RoutineSpecification {
	private Duration duration;
	private final GlindaTimePoint startTime;

	public CustomDurationRoutineSpecification(BracketedString name, Duration duration,
			GlindaTimePoint startTime) {
		super(name);
		this.startTime = startTime;
		this.duration = duration;
	}

	public GlindaTimePoint lastInstanceBefore(GlindaTimePoint now) {
		GlindaTimePoint previous = startTime;
		GlindaTimePoint next = previous;
		while (now.isAfter(next)) {
			previous = next;
			next = next.plus(duration);
		}
		return previous;
	}

	public GlindaTimePoint firstInstanceAfter(GlindaTimePoint now) {
		GlindaTimePoint before = lastInstanceBefore(now);
		if (before.isAfter(now))
			return before;
		return before.plus(duration);
	}
}
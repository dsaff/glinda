package net.saff.glinda.book;

import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;

public class DurationWaitTime implements WaitTime {
	private final Duration duration;

	public DurationWaitTime(Duration duration) {
		this.duration = duration;
	}

	public GlindaTimePoint timeToStopWaiting(GlindaTimePoint now) {
		return now.plus(duration);
	}
	
	@Override
	public String toString() {
		return "for " + duration;
	}
}

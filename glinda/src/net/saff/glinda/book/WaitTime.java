package net.saff.glinda.book;

import net.saff.glinda.time.GlindaTimePoint;

public interface WaitTime {
	public abstract GlindaTimePoint timeToStopWaiting(GlindaTimePoint now);
}

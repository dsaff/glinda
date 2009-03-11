/**
 * 
 */
package net.saff.glinda.projects.routines;

import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;

public enum TimeDirection {
	forward {
		@Override public boolean aFollowsB(GlindaTimePoint a, GlindaTimePoint b) {
			return isDirectionFromAToB(a, b);
		}

		@Override public boolean isDirectionFromAToB(GlindaTimePoint a,
				GlindaTimePoint b) {
			if (b == null)
				return false;
			return b.isAfter(a);
		}

		@Override public GlindaTimePoint push(GlindaTimePoint now,
				Duration duration) {
			if (now == null)
				return null;
			return now.plus(duration);
		}
	},
	backward {
		@Override public boolean aFollowsB(GlindaTimePoint a, GlindaTimePoint b) {
			return isDirectionFromAToB(a, b);
		}

		@Override public boolean isDirectionFromAToB(GlindaTimePoint a,
				GlindaTimePoint b) {
			return b.isBefore(a);
		}

		@Override public GlindaTimePoint push(GlindaTimePoint now,
				Duration duration) {
			return now.minus(duration);
		}
	};

	public abstract boolean aFollowsB(GlindaTimePoint a, GlindaTimePoint b);

	public abstract boolean isDirectionFromAToB(GlindaTimePoint a,
			GlindaTimePoint b);

	public abstract GlindaTimePoint push(GlindaTimePoint now, Duration duration);

	GlindaTimePoint pushPast(GlindaTimePoint pushThis,
			GlindaTimePoint pastThis, Duration byThisDuration) {
		if (pastThis.equals(pushThis)
				|| isDirectionFromAToB(pastThis, pushThis))
			return pushThis;
		return push(pushThis, byThisDuration);
	}
}
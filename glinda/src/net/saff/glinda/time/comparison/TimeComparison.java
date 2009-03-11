/**
 * 
 */
package net.saff.glinda.time.comparison;

import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.Duration;

public enum TimeComparison {
	before {
		@Override
		public int adjustmentFactor() {
			return 1;
		}

		@Override
		public GlindaTimePoint apply(GlindaTimePoint reference, Duration duration) {
			return reference.minus(duration);
		}
	},
	after {
		@Override
		public int adjustmentFactor() {
			return -1;
		}

		@Override
		public GlindaTimePoint apply(GlindaTimePoint reference, Duration duration) {
			return reference.plus(duration);
		}
	};

	public int adjustedComparison(final GlindaTimePoint t,
			GlindaTimePoint dateToCompare, Duration d) {
		return apply(t, d).compareTo(dateToCompare)
				* adjustmentFactor();
	}

	public abstract int adjustmentFactor();

	public abstract GlindaTimePoint apply(GlindaTimePoint t, Duration duration);
}
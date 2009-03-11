/**
 * 
 */
package net.saff.glinda.time;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimePoint;

public class Never extends GlindaTimePoint {
	public static GlindaTimePoint INSTANCE = new Never();

	public TimePoint getPoint() {
		throw new UnsupportedOperationException();
	}

	public GlindaTimePoint plus(Duration duration) {
		return this;
	}

	public GlindaTimePoint whenShouldItHappenNext(
			Duration desiredTimeBetweenTracks) {
		return new GlindaTimePoint() {
			public TimePoint getPoint() {
				throw new UnsupportedOperationException();
			};

			public String displayRelativeTo(GlindaTimePoint now) {
				return "now";
			}
		};
	}

	public String displayRelativeTo(GlindaTimePoint now) {
		return "never";
	}

	@Override public boolean equals(Object arg0) {
		return this == arg0;
	}

	@Override public String toString() {
		return "never";
	}
}
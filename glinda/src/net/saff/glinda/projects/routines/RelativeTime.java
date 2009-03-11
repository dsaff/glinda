/**
 * 
 */
package net.saff.glinda.projects.routines;

import static com.domainlanguage.time.Duration.weeks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.saff.glinda.time.GlindaTimePoint;

public class RelativeTime implements Serializable {
	private String timeString;

	public RelativeTime(String timeString) {
		this.timeString = timeString;
	}

	public GlindaTimePoint before(GlindaTimePoint now) {
		return push(now, TimeDirection.backward);
	}

	public GlindaTimePoint after(GlindaTimePoint now) {
		if (timeString.contains("next"))
			return new RelativeTime(timeString.replace("next ", "")).after(now)
					.plus(weeks(1));

		return push(now, TimeDirection.forward);
	}

	public GlindaTimePoint push(GlindaTimePoint now, TimeDirection direction) {
		GlindaTimePoint pushed = now;
		for (RelativeTimeUnit each : RelativeTimeUnit.values())
			pushed = each.push(pushed, direction, timeString);
		return pushed;
	}

	@Override public String toString() {
		return timeString;
	}

	public List<GlindaTimePoint> nextNInstances(GlindaTimePoint now, int howMany) {
		ArrayList<GlindaTimePoint> times = new ArrayList<GlindaTimePoint>();
		GlindaTimePoint mostRecent = now;
		for (int i = 0; i < howMany; i++) {
			mostRecent = after(mostRecent);
			times.add(mostRecent);
		}
		return times;
	}
}
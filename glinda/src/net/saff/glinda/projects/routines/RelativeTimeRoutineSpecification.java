/**
 * 
 */
package net.saff.glinda.projects.routines;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.time.GlindaTimePoint;

public class RelativeTimeRoutineSpecification extends RoutineSpecification {
	private RelativeTime rtime;

	public RelativeTimeRoutineSpecification(BracketedString name, RelativeTime rtime) {
		super(name);
		this.rtime = rtime;
	}

	public RelativeTimeRoutineSpecification(BracketedString name, String string) {
		this(name, new RelativeTime(string));
	}

	public GlindaTimePoint lastInstanceBefore(GlindaTimePoint now) {
		return rtime.before(now);
	}

	public GlindaTimePoint firstInstanceAfter(GlindaTimePoint now) {
		return rtime.after(now);
	}

	@Override public String toString() {
		return rtime.toString();
	}
}
package net.saff.glinda.projects.routines;




import java.io.Serializable;

import net.saff.glinda.names.BracketedString;
import net.saff.glinda.time.GlindaTimePoint;

public abstract class RoutineSpecification implements Serializable {
	private BracketedString name;

	public RoutineSpecification(BracketedString name) {
		this.name = name;
	}
	
	public abstract GlindaTimePoint lastInstanceBefore(GlindaTimePoint now);

	public abstract GlindaTimePoint firstInstanceAfter(GlindaTimePoint now);

	public BracketedString getRoutineName() {
		return name;
	}
}

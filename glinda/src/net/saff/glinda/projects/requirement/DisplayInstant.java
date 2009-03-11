package net.saff.glinda.projects.requirement;

import java.util.EnumSet;

import net.saff.glinda.time.GlindaTimePoint;

public interface DisplayInstant {
	GlindaTimePoint getNow();

	String display(GlindaTimePoint time);

	EnumSet<Flag> getFlags();
}

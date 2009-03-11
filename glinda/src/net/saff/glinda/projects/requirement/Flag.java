package net.saff.glinda.projects.requirement;

import java.util.EnumSet;

public enum Flag {
	withTimes, weirdFlag, actionItemsFirst, withParents, showFullTimes, distinguishTrips, topTen;
	
	public static final EnumSet<Flag> NONE = EnumSet.noneOf(Flag.class);

}

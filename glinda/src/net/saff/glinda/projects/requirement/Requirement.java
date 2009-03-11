/**
 * 
 */
package net.saff.glinda.projects.requirement;

import java.io.Serializable;

import net.saff.glinda.time.GlindaTimePoint;

import com.domainlanguage.time.TimeInterval;

public class Requirement<T extends RequirementNode<T>> implements Serializable {
	public static <U extends RequirementNode<U>, V extends U> Requirement<U> hardForever(
			V requirer, V prerequisite, GlindaTimePoint start) {
		return new Requirement<U>(requirer, RequirementType.HARD, prerequisite, start
				.until(null));
	}

	private static int latestSerialNumber = 0;


	private final T requirer;
	private final T required;
	private final RequirementType type;
	private final GlindaTimePoint expiration;

	private final int serialNumber;

	private final GlindaTimePoint creationTime;

	public Requirement(T requirer, RequirementType type, T required, TimeInterval lifespan) {
		if (type == null)
			throw new NullPointerException();
		this.requirer = requirer;
		this.required = required;
		this.type = type;
		this.creationTime = GlindaTimePoint.from(lifespan.start());
		this.expiration = GlindaTimePoint.from(lifespan.end());
		this.serialNumber = latestSerialNumber++;
		if (getCreationTime() == null)
			throw new NullPointerException();
	}

	public boolean isExpiredOrDone(GlindaTimePoint now) {
		return isDone(now) || hasExpired(now);
	}

	public boolean hasExpired(GlindaTimePoint now) {
		return (expiration != null && now.isAfter(expiration));
	}

	public boolean isDone(GlindaTimePoint now) {
		return noPrerequsite() || doneBefore(now);
	}

	public boolean doneBefore(GlindaTimePoint now) {
		return getPrerequisite().doneBetween(getCreationTime(), now);
	}

	public boolean noPrerequsite() {
		return getPrerequisite() == null;
	}

	public T getPrerequisite(GlindaTimePoint now) {
		if (isExpiredOrDone(now))
			return null;
		return required;
	}

	public String leftArrow() {
		return type.leftArrow();
	}

	public Requirement<T> harden(GlindaTimePoint now) {
		if (isExpiredOrDone(now))
			return copy(now);
		return Requirement.hardForever(requirer, getPrerequisite(now), now);
	}

	public int serialNumber() {
		return serialNumber;
	}

	public boolean alwaysBlocksParent() {
		return type.alwaysBlocksParent();
	}

	public GlindaTimePoint getCreationTime() {
		return creationTime;
	}

	public RequirementType getType() {
		return type;
	}

	public T getPrerequisiteRegardlessOfApplicability() {
		return getPrerequisite();
	}

	public GlindaTimePoint getExpiration() {
		return expiration;
	}

	public Requirement<T> copy(GlindaTimePoint now) {
		return new Requirement<T>(requirer, type, getPrerequisite(), now
				.until(shiftExpirationTo(now)));
	}

	private GlindaTimePoint shiftExpirationTo(GlindaTimePoint now) {
		if (expiration == null)
			return null;
		return now.plus(creationTime.until(expiration).length());
	}

	public T getPrerequisiteEvenIfExpired(GlindaTimePoint now) {
		if (isDone(now))
			return null;
		return getPrerequisite();
	}

	public String rightArrow(GlindaTimePoint now) {
		String rightArrow = getType().rightArrow();
		if (isExpiredOrDone(now))
			return "-/->";
		return rightArrow;
	}

	public T getPrerequisite() {
		return required;
	}

	public String statusName(DisplayInstant instant) {
		RequirementNode<?> prerequisite = getPrerequisite(instant.getNow());
		if (prerequisite == null)
			return null;
		return prerequisite.nameForStatusLine(instant.getFlags());
	}

	public String blockedComment(DisplayInstant instant) {
		GlindaTimePoint now = instant.getNow();
		T prerequisite = getPrerequisite(now);
		if (prerequisite == null)
			return null;
		if (prerequisite.doneBetween(getCreationTime(),
				instant.getNow()))
			return null;
		return getType().waitingReason(
				statusName(instant),
				instant.display(getExpiration()));
	}

	public TimeInterval getLifespan() {
		return creationTime.until(expiration);
	}
	
	@Override
	public String toString() {
		return rightArrow(GlindaTimePoint.now()) + " " + required;
	}

	public T getRequirer() {
		return requirer;
	}
}
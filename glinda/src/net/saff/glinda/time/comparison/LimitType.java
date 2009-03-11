package net.saff.glinda.time.comparison;

public enum LimitType {
	CLOSED, OPEN;

	public boolean isOpen() {
		return equals(LimitType.OPEN);
	}
}

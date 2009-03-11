package net.saff.glinda.time.comparison;

public enum Comparison {
	LESS, MORE;

	public boolean isMore() {
		return equals(MORE);
	}

}

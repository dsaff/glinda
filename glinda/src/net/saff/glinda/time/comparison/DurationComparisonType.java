package net.saff.glinda.time.comparison;

public enum DurationComparisonType {
	more_than(LimitType.CLOSED, Comparison.MORE) {
	},
	at_least(LimitType.OPEN, Comparison.MORE) {
	},
	at_most(LimitType.OPEN, Comparison.LESS) {
	},
	less_than(LimitType.CLOSED, Comparison.LESS) {
	};

	private final Comparison comparison;
	private final LimitType limitType;

	private DurationComparisonType(LimitType limitType, Comparison comparison) {
		this.limitType = limitType;
		this.comparison = comparison;
	}

	@Override
	public String toString() {
		return super.toString().replace('_', ' ');
	}

	boolean shouldAllowComparison(int adjustedComparison) {
		if (adjustedComparison == 0)
			return limitType.isOpen();
		return comparison.isMore() ^ adjustedComparison < 0;
	}
}

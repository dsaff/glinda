package net.saff.glinda.projects.goals;

public abstract class GoalValue {
	public static final GoalValue NULL = new GoalValue() {
		public double asDouble() {
			throw new IllegalStateException();
		}

		public String toString() {
			return "null";
		}

		public boolean isNull() {
			return true;
		}

		public boolean sameOrLessThan(GoalValue yesterday) {
			return yesterday.isNull();
		}
	};
	
	public static class TrackedGoalValue extends GoalValue {
		private final double value; 
		private final boolean isVelocity;

		private TrackedGoalValue(double value, boolean isVelocity) {
			this.value = value;
			this.isVelocity = isVelocity;
		}

		private TrackedGoalValue(double value) {
			this(value, false);
		}

		@Override public String toString() {
			if (isVelocity)
				return String.format("%.2f/day", asDouble());
			return String.valueOf(asDouble()).replaceFirst("\\.0$", "");
		}

		@Override public double asDouble() {
			return value;
		}
	}

	public static GoalValue absolute(double value) {
		return new TrackedGoalValue(value);
	}

	public static GoalValue of(double doubleAtExactTime, boolean compareVelocity) {
		return new TrackedGoalValue(doubleAtExactTime, compareVelocity);
	}
	
	public abstract double asDouble();

	public boolean isNull() {
		return false;
	}

	public boolean sameOrLessThan(GoalValue yesterday) {
		if (yesterday.isNull())
			return false;
		return howMuchMoreThan(yesterday) <= 0;
	}

	private double howMuchMoreThan(GoalValue yesterday) {			
		return asDouble() - yesterday.asDouble();
	}

	public boolean lessThan(GoalValue yesterday) {
		return howMuchMoreThan(yesterday) < 0;
	}
	
	@Override public boolean equals(Object obj) {
		if (! (obj instanceof GoalValue))
			return false;
		GoalValue g = (GoalValue) obj;
		if (isNull())
			return g.isNull();
		return Math.abs(asDouble() - g.asDouble()) < 0.001;
	}
	
	@Override public int hashCode() {
		return 1;
	}
}
/**
 * 
 */
package net.saff.glinda.projects.requirement;

public enum RequirementType {
	HARD("=") {
		@Override public String waitingReason(String statusName,
				String expirationString) {
			return "next step: " + statusName;
		}

		@Override public boolean alwaysBlocksParent() {
			return true;
		}
	},

	SOFT("-") {
		@Override public String waitingReason(String statusName,
				String expirationString) {
			return String.format("first: %s until %s", statusName,
					expirationString);
		}

		@Override public boolean alwaysBlocksParent() {
			return false;
		}
	};

	private final String arrowBody;

	private RequirementType(String arrowBody) {
		this.arrowBody = arrowBody;
	}

	public abstract String waitingReason(String statusName,
			String expirationString);

	public abstract boolean alwaysBlocksParent();

	public String leftArrow() {
		return "<" + arrowBody;
	}

	public String rightArrow() {
		return arrowBody + ">";
	}
}
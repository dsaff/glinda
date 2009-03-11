/**
 * 
 */
package net.saff.glinda.projects.tasks;

import net.saff.glinda.projects.core.Project;
import net.saff.glinda.projects.requirement.RequirementNode;

public abstract class ChainingRule {
	private static final class BranchingChainingRule extends ChainingRule {
		private String extraLeftSpace;

		private BranchingChainingRule(String extraLeftSpace) {
			this.extraLeftSpace = extraLeftSpace;
		}

		public String extraLeftSpace() {
			return extraLeftSpace;
		}

		public ChainingRule indent(RequirementNode<?> blockedItem) {
			if (!blockedItem.shouldShowBranchedParents())
				return noBranching();
			return new BranchingChainingRule(extraLeftSpace + "  ");
		}

		@Override
		protected String oneOrMoreParents(BlockedAncestry blockedAncestry) {
			String parents = "";
			for (RequirementNode<Project> blockedItem : blockedAncestry
					.getDirectlyBlocked())
				parents += "\n "
						+ extraLeftSpace()
						+ indent(blockedItem).itemAndParents(
								blockedAncestry.with(blockedItem),
								blockedAncestry.currentName());
			return parents;
		}
	}

	public static ChainingRule noBranching() {
		return new ChainingRule() {

			@Override
			protected String oneOrMoreParents(BlockedAncestry blockedAncestry) {
				if (blockedAncestry.getDirectlyBlocked().size() > 1)
					return multipleParents(blockedAncestry);
				return singleParent(blockedAncestry);
			}

			public String multipleParents(BlockedAncestry blockedAncestry) {
				return itemAndParents(blockedAncestry.firstParentOfMany(),
						blockedAncestry.currentName());
			}

			private String singleParent(BlockedAncestry blockedAncestry) {
				return itemAndParents(blockedAncestry.onlyParent(),
						blockedAncestry.currentName());
			}
		};
	}

	public static ChainingRule branching(String extraLeftSpace) {
		return new BranchingChainingRule(extraLeftSpace);
	}

	public static ChainingRule branching() {
		return branching("");
	}

	public String parents(BlockedAncestry blockedAncestry) {
		if (blockedAncestry.getDirectlyBlocked().isEmpty())
			return "";
		return oneOrMoreParents(blockedAncestry);
	}

	protected abstract String oneOrMoreParents(BlockedAncestry blockedAncestry);

	public String itemAndParents(BlockedAncestry blockedAncestry,
			String lastName) {
		if (blockedAncestry.isCycle())
			return blockedAncestry.leftArrowed(lastName, "[Broke cycle]");

		return blockedAncestry.leftArrowed(blockedAncestry.lineageTop())
				+ parents(blockedAncestry);
	}

}
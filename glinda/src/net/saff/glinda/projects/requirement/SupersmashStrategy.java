package net.saff.glinda.projects.requirement;

import java.util.List;

/**
 * @author saff
 * A strategy for removing common elements from lists
 */
public interface SupersmashStrategy {
	public abstract <U> void removeCommonElements(List<U> theseChildren,
			List<U> theseParents);
}
package net.saff.glinda.projects.requirement;

import java.util.Iterator;
import java.util.List;

public class IterateSupersmashStrategy implements SupersmashStrategy {
	public IterateSupersmashStrategy() {
		
	}
	
	/* (non-Javadoc)
	 * @see net.saff.glinda.projects.requirement.SupersmashStrategy#removeCommonElements(java.util.List, java.util.List)
	 */
	public <U> void removeCommonElements(List<U> theseChildren, List<U> theseParents) {
		for (Iterator<U> iterator = theseChildren.iterator(); iterator
				.hasNext();) {
			U childR = iterator.next();
			for (Iterator<U> iterator2 = theseParents.iterator(); iterator2
					.hasNext();) {
				U parentR = iterator2.next();
				if (childR == parentR) {
					iterator.remove();
					iterator2.remove();
				}
			}
		}
	}
	
	@Override
	public String toString() {
	  return "Iterate Strategy";
	}
}
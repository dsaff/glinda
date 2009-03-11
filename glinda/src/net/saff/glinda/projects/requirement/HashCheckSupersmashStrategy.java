package net.saff.glinda.projects.requirement;

import java.util.HashSet;
import java.util.List;

public class HashCheckSupersmashStrategy implements SupersmashStrategy {
  private final SupersmashStrategy delegate;
  private boolean shouldHashBiggerList;

  public HashCheckSupersmashStrategy(SupersmashStrategy delegate,
      boolean hashBiggerList) {
    this.delegate = delegate;
    this.shouldHashBiggerList = hashBiggerList;
  }

  public <U> void removeCommonElements(List<U> smallerList, List<U> biggerList) {
    if (hasCommonElements(smallerList, biggerList))
      delegate.removeCommonElements(smallerList, biggerList);
  }

  private <U> boolean hasCommonElements(List<U> hashedList, List<U> iteratedList) {
    boolean hashedIsBigger = hashedList.size() > iteratedList.size();
    if (hashedList.size() != iteratedList.size()
        && hashedIsBigger != shouldHashBiggerList)
      return hasCommonElements(iteratedList, hashedList);

    // TODO: get todo tags
    // TODO: bigger or smaller is faster?
    HashSet<U> set = new HashSet<U>(hashedList);
    for (U each : iteratedList)
      if (set.contains(each)) return true;
    return false;
  }

  @Override
  public String toString() {
    return "Hash Check Strategy, hashing list: "
        + (shouldHashBiggerList ? "bigger" : "smaller");
  }
}

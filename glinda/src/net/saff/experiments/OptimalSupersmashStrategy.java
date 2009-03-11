package net.saff.experiments;

import java.util.Arrays;

import net.saff.glinda.core.GlindaInvocation;
import net.saff.glinda.projects.requirement.HashCheckSupersmashStrategy;
import net.saff.glinda.projects.requirement.IterateSupersmashStrategy;
import net.saff.glinda.projects.requirement.SupersmashStrategy;

public class OptimalSupersmashStrategy {
  public static void main(String[] args) throws Exception {
    IterateSupersmashStrategy istrategy = new IterateSupersmashStrategy();
    HashCheckSupersmashStrategy hstrategyHashBig =
        new HashCheckSupersmashStrategy(istrategy, true);
    HashCheckSupersmashStrategy hstrategyHashSmall =
        new HashCheckSupersmashStrategy(istrategy, false);
    for (int i = 0; i < 5; i++)
      for (SupersmashStrategy each : Arrays.asList(hstrategyHashBig,
          hstrategyHashSmall, istrategy)) {
        long startTime = System.currentTimeMillis();
        // TODO: DUP?
        runStatus("153k_journal.txt", each);
        System.out.println(each + ": "
            + (System.currentTimeMillis() - startTime));
      }
  }

  // TODO: unstatic
  // TODO: DUP
  private static String runStatus(String journalFileName,
      SupersmashStrategy supersmashStrategy) throws Exception {
    // TODO: pass-through
    return invocation(journalFileName, supersmashStrategy).output();
  }

  private static GlindaInvocation invocation(String journalFileName,
      SupersmashStrategy supersmashStrategy) {
    String[] commands =
        {"status", "-withTimes", "-actionItemsFirst", "-withParents"};
    return GlindaInvocation.fromLocationAndStrategy(
        resourceFile(journalFileName), supersmashStrategy, commands);
  }

  private static String resourceFile(String journalFileName) {
    return "src/test/resources/" + journalFileName;
  }
}

/**
 * 
 */
package net.saff.glinda.journal;

import java.lang.reflect.Method;

class ImportRequest {
  public Class<?> type;
  public String methodName;

  public ImportRequest(Class<?> type, String methodName) {
    this.type = type;
    this.methodName = methodName;
  }

  Method findMethodWithFewestParams() throws NoSuchMethodException {
    Method bestMethod = null;
    int leastParams = Integer.MAX_VALUE;
    for (Method each : type.getMethods()) {
      int paramCount = each.getParameterTypes().length;
      if (each.getName().equals(methodName) && paramCount < leastParams) {
        bestMethod = each;
        leastParams = paramCount;
      }
    }

    if (bestMethod == null) return throwNoSuchMethodException();
    return bestMethod;
  }

  private Method throwNoSuchMethodException() throws NoSuchMethodException {
    return type.getMethod(methodName);
  }
}

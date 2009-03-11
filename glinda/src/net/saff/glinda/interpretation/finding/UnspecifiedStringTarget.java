/**
 * 
 */
package net.saff.glinda.interpretation.finding;

import net.saff.glinda.book.TypeSpecificParser;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;

import java.lang.reflect.Type;


public class UnspecifiedStringTarget extends StringTarget<Object> {
  // TODO (Apr 20, 2008 1:54:17 AM): move to StringTarget

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // TODO: pattern of returning a single parser
  private static class DefaultParser implements TypeSpecificParserFactory {
    public static final DefaultParser INSTANCE = new DefaultParser();


    public TypeSpecificParser parserFor(Type type) {
      return new TypeSpecificParser() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public Object parse(String string) {
          return string;
        }
      };
    }
  }

  static TypeSpecificParserFactory parserFor(Object target) {
    if (target instanceof TypeSpecificParserFactory) return (TypeSpecificParserFactory) target;
    return DefaultParser.INSTANCE;
  }

  // TODO (Apr 20, 2008 2:07:25 AM): remove all implementation?
  // TODO (Apr 20, 2008 2:07:43 AM): tests slow



  public UnspecifiedStringTarget(Object target, TypeSpecificParserFactory parser) {
    super(target, parser);
  }

  public UnspecifiedStringTarget(Object target) {
    this(target, UnspecifiedStringTarget.parserFor(target));
  }

  @Override
  public String toString() {
    return getTarget().toString();
  }
}

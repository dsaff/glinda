package net.saff.glinda.book;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.EnumSet;

import net.saff.glinda.ideas.search.KeyValuePair;
import net.saff.glinda.ideas.search.SearchCriteria;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;
import net.saff.glinda.names.BracketedString;
import net.saff.glinda.projects.requirement.Flag;
import net.saff.glinda.projects.routines.DurationString;
import net.saff.glinda.projects.routines.RelativeTime;
import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.GlindaTimePointParser;

import com.domainlanguage.time.Duration;

public class StaticParser implements TypeSpecificParserFactory, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Object parse(String string, Type type) throws ParseException {
    return parserFor(type).parse(string);
  }

  public TypeSpecificParser parserFor(Type type) {
    if (type == BracketedString.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return new BracketedString(string);
      }
    };
    if (type == GlindaTimePoint.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return new GlindaTimePointParser().parse(string);
      }
    };
    if (type == SearchCriteria.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return KeyValuePair.from(string);
      }
    };
    if (type == int.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return Integer.valueOf(string);
      }
    };
    if (type == boolean.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return Boolean.valueOf(string);
      }
    };
    if (type == double.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return Double.valueOf(string);
      }
    };
    if (type == Duration.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return new DurationString(string).parse();
      }
    };
    if (type == WaitTime.class) return new TypeSpecificParser() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Object parse(String string) {
        return parseWaitTime(string);
      }
    };
    if (type instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType) type;
      if (ptype.getRawType() == EnumSet.class) return new TypeSpecificParser() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public Object parse(String string) {
          return parseFlags(string);
        }
        // assumes
        // EnumSet<Flag>,
        // but
        // that's
        // all we have for now.
      };
    }
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

  private WaitTime parseWaitTime(final String string) {
    try {
      Duration duration = new DurationString(string).parse();
      return new DurationWaitTime(duration);
    } catch (Exception e) {

    }

    return new WaitTime() {
      public GlindaTimePoint timeToStopWaiting(GlindaTimePoint now) {
        return new RelativeTime(string).after(now);
      }

      @Override
      public String toString() {
        return string;
      }
    };
  }

  private EnumSet<Flag> parseFlags(String concatenated) {
    if (concatenated == null) return Flag.NONE;
    EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
    for (String each : concatenated.split(" ")) {
      flags.add(Flag.valueOf(each.substring(1)));
    }
    return flags;
  }
}

package test.net.saff.glinda.user.k_pragmas;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.saff.glinda.book.TypeSpecificParser;
import net.saff.glinda.interpretation.invoking.TypeSpecificParserFactory;

import org.junit.Test;

import test.net.saff.glinda.testclasstypes.CondCorrespondentOnDiskTest;

public class HowDoesImport extends CondCorrespondentOnDiskTest {
  public static class BufferList implements TypeSpecificParserFactory {
    private List<StringBuffer> buffers = new ArrayList<StringBuffer>();

    public void create(String newString) {
      buffers.add(new StringBuffer(newString));
    }

    public List<String> buffers() {
      ArrayList<String> strings = new ArrayList<String>();
      for (StringBuffer each : buffers) {
        strings.add(each.toString());
      }
      return strings;
    }

    public TypeSpecificParser parserFor(final Type type) {
      return new TypeSpecificParser() {

        public Object parse(String string) throws ParseException {
          if (type == String.class) return string;

          if (type == int.class) return Integer.valueOf(string);

          for (StringBuffer each : buffers)
            if (each.toString().equals(string)) return each;

          throw new ParseException(String.format("Could not parse %s into %s",
              string, type), 0);
        }
      };
    }
  }

  @Test
  public void includeOtherTargets() {
    r("##target " + BufferList.class.getName());
    r("##import java.lang.StringBuffer.reverse");
    r("#create abc");
    r("#create xyz");
    r("#reverse abc");
    run("buffers");
    w("buffers:");
    w("cba");
    w("xyz");
  }

  @Test
  public void includeMethodsWithParameters() {
    r("##target " + BufferList.class.getName());
    r("##import java.lang.StringBuffer.deleteCharAt");
    r("#create abc");
    r("#create xyz");
    r("#deleteCharAt abc 0");
    run("buffers");
    w("buffers:");
    w("bc");
    w("xyz");
  }
}

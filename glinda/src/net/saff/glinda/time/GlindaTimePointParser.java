/**
 * 
 */
package net.saff.glinda.time;

import static java.util.Arrays.asList;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.domainlanguage.time.TimePoint;

public class GlindaTimePointParser {
	static class LazyGlindaTimePoint extends GlindaTimePoint {
		private static final List<SimpleDateFormat> POTENTIAL_FORMATS = asList(
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
				new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"),
				new SimpleDateFormat("MM-dd HH:mm"), new SimpleDateFormat(
						"MMM d"));

		private final String string;
		private TimePoint point = null;

		public LazyGlindaTimePoint(String string) {
			this.string = string;
			if (string == null)
				throw new NullPointerException();
		}

		@Override
		public TimePoint getPoint() {
			if (point == null)
				for (SimpleDateFormat each : POTENTIAL_FORMATS) {
					Date parse = each.parse(string, new ParsePosition(0));
					if (parse != null) {
						point = TimePoint.from(parse);
						return point;
					}
				}

			return point;
		}
	}

	public GlindaTimePoint parse(String string) {
		if (string == null)
			return null;
		return new LazyGlindaTimePoint(string);
	}

	public GlindaTimePoint moveToDate(GlindaTimePoint startTime,
			String dateString) {
		GlindaTimePoint parsed = parse(dateString);
		return GlindaTimePoint.from(new GregorianCalendar(startTime.getYear(),
				parsed.getMonth(), parsed.getDayOfMonth(), startTime
						.getHourOfDay(), startTime.getMinute(), startTime
						.getSeconds()));
	}
}
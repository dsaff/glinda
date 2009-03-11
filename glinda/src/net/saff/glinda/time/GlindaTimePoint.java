package net.saff.glinda.time;

import static com.domainlanguage.time.Duration.days;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.domainlanguage.time.Duration;
import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;
import com.domainlanguage.timeutil.SystemClock;

public abstract class GlindaTimePoint implements Comparable<GlindaTimePoint>, Serializable {
  private static class ConcreteGlindaTimePoint extends GlindaTimePoint {
    private final TimePoint point;

    private ConcreteGlindaTimePoint(TimePoint point) {
      this.point = point != null ? point : SystemClock.now();
    }

    @Override
    public TimePoint getPoint() {
      return point;
    }
  }

  public static GlindaTimePoint now() {
    return new ConcreteGlindaTimePoint(SystemClock.now());
  }

  public static GlindaTimePoint from(Calendar calendar) {
    return new ConcreteGlindaTimePoint(TimePoint.from(calendar));
  }

  public int getYear() {
    return getField(Calendar.YEAR);
  }

  public int getHourOfDay() {
    return getField(Calendar.HOUR_OF_DAY);
  }

  public int getField(int field) {
    return getPoint().asJavaCalendar(TimeZone.getDefault()).get(field);
  }

  public GlindaTimePoint minus(Duration duration) {
    return new ConcreteGlindaTimePoint(getPoint().minus(duration));
  }

  public GlindaTimePoint plus(Duration duration) {
    return new ConcreteGlindaTimePoint(getPoint().plus(duration));
  }

  public String toString(String pattern) {
    return getPoint().toString(pattern, TimeZone.getDefault());
  }

  public boolean isSameDayAs(GlindaTimePoint then) {
    return getPoint().isSameDayAs(then.getPoint(), TimeZone.getDefault());
  }

  public boolean isAfter(GlindaTimePoint glindaTimePoint) {
    return getPoint().isAfter(glindaTimePoint.getPoint());
  }

  public TimeInterval until(GlindaTimePoint now) {
    return getPoint().until(now == null ? null : now.getPoint());
  }

  public int compareTo(GlindaTimePoint dateToCompare) {
    return getPoint().compareTo(dateToCompare.getPoint());
  }

  public boolean isSameYearAs(GlindaTimePoint then) {
    return getYear() == then.getYear();
  }

  public String displayRelativeTo(GlindaTimePoint now) {
    return toString(formatString(now));
  }

  private String formatString(GlindaTimePoint now) {
    if (isSameDayAs(now)) return "HH:mm";
    if (isSameYearAs(now)) return "MM-dd HH:mm";
    return "yyyy-MM-dd HH:mm";
  }

  public String longString() {
    return toString("yyyy-MM-dd HH:mm:ss");
  }

  public double daysUntil(GlindaTimePoint timePoint) {
    return until(timePoint).length().dividedBy(days(1)).decimalValue(10,
        BigDecimal.ROUND_HALF_EVEN).doubleValue();
  }

  @Override
  public String toString() {
    return longString();
  }

  public abstract TimePoint getPoint();

  public GlindaTimePoint whenShouldItHappenNext(
      Duration desiredTimeBetweenTracks) {
    return plus(desiredTimeBetweenTracks);
  }

  public int getMonth() {
    return getField(Calendar.MONTH);
  }

  public int getDayOfMonth() {
    return getField(Calendar.DAY_OF_MONTH);
  }

  public String timeWhenOneIsReachedAtThisRate(double rate) {
    return plus(days((int) (1.0 / rate))).displayRelativeTo(this);
  }

  public boolean isBefore(GlindaTimePoint other) {
    return other.isAfter(this);
  }

  public int getMinute() {
    return getField(Calendar.MINUTE);
  }

  public int getSeconds() {
    return getField(Calendar.SECOND);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    GlindaTimePoint g = (GlindaTimePoint) obj;
    try {
      TimePoint point = g.getPoint();
      return point.equals(getPoint());
    } catch (UnsupportedOperationException e) {
      return g.equals(this);
    }
  }

  public GlindaTimePoint moveToDayOfMonth(int dayOfMonth) {
    return GlindaTimePoint.from(new GregorianCalendar(getYear(), getMonth(),
        dayOfMonth, getHourOfDay(), getMinute(), getSeconds()));
  }

  public GlindaTimePoint moveToHourOfDay(int hourOfDay) {
    return GlindaTimePoint.from(new GregorianCalendar(getYear(), getMonth(),
        getDayOfMonth(), hourOfDay, 0, 0));
  }

  public boolean isAfterOrSame(GlindaTimePoint creationTime) {
    return isAfter(creationTime) || equals(creationTime);
  }

  public boolean isBeforeOrSame(GlindaTimePoint now) {
    return isBefore(now) || equals(now);
  }

  public static GlindaTimePoint from(TimePoint point) {
    if (point == null) return null;
    return new ConcreteGlindaTimePoint(point);
  }

  public GlindaTimePoint moveToMeridianString(String timeString2) {
    return moveToHourOfDay(hourOfDay(timeString2));
  }

  private int hourOfDay(String timeString) {
    Matcher matcher =
        Pattern.compile(".*?([0-9]+)([ap]m)$").matcher(timeString);
    if (matcher.matches()) {
      String hourNumber = matcher.group(1);
      String meridianString = matcher.group(2);
      return new MeridianString(meridianString).interpretTime(hourNumber);
    }
    return 0;
  }

  public String asNiceDateString() {
    return toString("MMM d");
  }

  public static void main(String[] args) {
    System.out.println(TimeZone.getDefault() + " " + GlindaTimePoint.now());
    TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    System.out.println(TimeZone.getDefault() + " " + GlindaTimePoint.now());
  }
}

package net.saff.glinda.projects.goals;

import static com.domainlanguage.time.Duration.days;
import static com.domainlanguage.time.Duration.seconds;

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import net.saff.glinda.time.GlindaTimePoint;
import net.saff.glinda.time.Never;

import com.domainlanguage.time.Duration;

public class TrackHistory implements Serializable {
  private TreeMap<GlindaTimePoint, Double> tracks =
      new TreeMap<GlindaTimePoint, Double>();

  private final GoalSettings settings = GoalSettings.defaults();

  boolean containsKey(GlindaTimePoint timePoint) {
    if (timePoint == null) return false;
    return tracks.containsKey(timePoint);
  }

  public Double track(GlindaTimePoint key, double value) {
    if (key == null) throw new NullPointerException();
    return tracks.put(key, value);
  }

  GlindaTimePoint timeYesterday(GoalComparisonInterval interval) {
    GlindaTimePoint latestEntry = latestEntry();
    GlindaTimePoint absoluteYesterday = interval.getThen();
    if (latestEntry != null && absoluteYesterday.isAfter(latestEntry))
      return latestEntry.plus(interval.getComparisonTimeFrame());
    return absoluteYesterday;
  }

  public GlindaTimePoint latestEntry() {
    if (isEmpty()) return null;
    return tracks.lastKey();
  }

  private TimedValue valueAtExactTime(GlindaTimePoint now) {
    if (isEmpty() || now == Never.INSTANCE || velocityHasntStartedYet(now))
      return new TimedValue(GoalValue.NULL, now);
    return new TimedValue(GoalValue.of(doubleAtExactTime(now),
        isCompareVelocity()), now);
  }

  private boolean velocityHasntStartedYet(GlindaTimePoint now) {
    return isCompareVelocity() && velocityBeginning().isAfter(now);
  }

  private boolean isCompareVelocity() {
    return settings.isCompareVelocity();
  }

  double doubleAtExactTime(GlindaTimePoint now) {
    if (false) return 0;

    if (!isCompareVelocity()) return absoluteValueAtTime(now);

    return velocityAtTime(now);
  }

  Double absoluteValueAtTime(GlindaTimePoint timePoint) {
    return tracks.get(timePoint);
  }

  double velocityAtTime(GlindaTimePoint timePoint) {
    Collection<Double> values =
        tracks.headMap(timePoint.plus(seconds(1))).values();
    return sum(values) / daysSinceBeginning(timePoint);
  }

  private double sum(Collection<Double> values) {
    double sum1 = 0;
    for (Double value : values) {
      sum1 += value;
    }
    return sum1;
  }

  double daysSinceBeginning(GlindaTimePoint timePoint) {
    return velocityBeginning().daysUntil(timePoint);
  }

  boolean isEmpty() {
    return tracks.isEmpty();
  }

  GlindaTimePoint velocityBeginning() {
    return tracks.firstKey().minus(days(1));
  }

  GlindaTimePoint timeToCheck(GlindaTimePoint now) {
    if (isCompareVelocity()) return now;
    return lastTrackBefore(now);
  }

  private GlindaTimePoint lastTrackBefore(GlindaTimePoint now) {
    if (containsKey(now)) return now;
    SortedMap<GlindaTimePoint, Double> headMap = tracks.headMap(now);
    if (headMap.isEmpty()) return Never.INSTANCE;
    return headMap.lastKey();
  }

  TimedValue valueAtTime(GlindaTimePoint now) {
    return valueAtExactTime(timeToCheck(now));
  }

  TimedValue yesterdaysValue(GoalComparisonInterval interval) {
    return valueAtTime(timeYesterday(interval));
  }

  AbsoluteValueDifference differenceSinceYesterday(
      GoalComparisonInterval interval) {
    TimedValue yesterday = yesterdaysValue(interval);
    TimedValue now = valueAtTime(interval.getNow());
    return new AbsoluteValueDifference(yesterday, now, settings, interval);
  }

  public Duration timeSinceLastTrack(GlindaTimePoint now) {
    // TODO: test in griping
    if (latestEntry().isAfter(now))
      throw new IllegalArgumentException(latestEntry() + " is after " + now);
    return latestEntry().until(now).length();
  }

  boolean tooLongSinceLastTrack(GlindaTimePoint now) {
    return timeSinceLastTrack(now).compareTo(
        settings.getDefaultComparisonTimeframe()) > 0;
  }

  public boolean velocityNeedsTrack(AbsoluteValueDifference difference) {
    return !(isEmpty()) && tooLongSinceLastTrack(difference.getNow())
        && !settings.isCompareVelocity();
  }

  public Duration getDefaultComparisonTimeframe() {
    return settings.getDefaultComparisonTimeframe();
  }

  public GoalSettings getSettings() {
    return settings;
  }

  public GlindaTimePoint timeYesterday(GlindaTimePoint now) {
    return timeYesterday(interval(now));
  }

  private GoalComparisonInterval interval(GlindaTimePoint now) {
    return new GoalComparisonInterval(now, settings
        .getDefaultComparisonTimeframe());
  }

  public TimedValue yesterdaysValue(GlindaTimePoint now) {
    return yesterdaysValue(interval(now));
  }

  public AbsoluteValueDifference differenceSinceYesterday(GlindaTimePoint now,
      Duration comparisonTimeFrame) {
    return differenceSinceYesterday(new GoalComparisonInterval(now,
        comparisonTimeFrame));
  }
}

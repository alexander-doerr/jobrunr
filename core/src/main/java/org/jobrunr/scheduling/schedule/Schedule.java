package org.jobrunr.scheduling.schedule;

import org.jobrunr.scheduling.schedule.cron.CronExpression;

import static java.time.Instant.now;

import java.time.*;

public abstract class Schedule implements Comparable<Schedule>{

  public final static int SMALLEST_SCHEDULE_IN_SECONDS = 5;

  /**
   * Calculates the next occurrence based on the current time (at UTC TimeZone).
   *
   * @return Instant of the next occurrence.
   */
  public Instant next() {
    return next(now(), ZoneOffset.UTC);
  }

  /**
   * Calculates the next occurrence based on the current time (at the given TimeZone).
   *
   * @return Instant of the next occurrence.
   */
  public Instant next(ZoneId zoneId) {
    return next(Instant.now(), zoneId);
  }

  /**
   * Calculates the next occurrence based on provided base time.
   *
   * @param baseInstant Instant object based on which calculating the next occurrence.
   * @return Instant of the next occurrence.
   */
  abstract public Instant next(Instant baseInstant, ZoneId zoneId);

  abstract public void validateSchedule();

  /**
   * Compare two {@code Schedule} objects based on next occurrence.
   * <p>
   * The next occurrences are calculated based on the current time.
   *
   * @param schedule the {@code Schedule} to be compared.
   * @return the value {@code 0} if this {@code Schedule} next occurrence is equal
   * to the argument {@code Schedule} next occurrence; a value less than
   * {@code 0} if this {@code Schedule} next occurrence is before the
   * argument {@code Schedule} next occurrence; and a value greater than
   * {@code 0} if this {@code Schedule} next occurrence is after the
   * argument {@code Schedule} next occurrence.
   */
  @Override
  public int compareTo(Schedule schedule) {
    if (schedule == this) {
      return 0;
    }

    Instant baseInstant = now();
    final Instant nextAnother = schedule.next(baseInstant, ZoneOffset.UTC);
    final Instant nextThis = this.next(baseInstant, ZoneOffset.UTC);

    return nextThis.compareTo(nextAnother);
  }
}

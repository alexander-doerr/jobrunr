package org.jobrunr.scheduling.schedule.interval;

import java.time.*;
import org.jobrunr.scheduling.schedule.*;

public class Interval extends Schedule{

  private Duration duration;

  public Interval(Duration duration){
    this.duration = duration;
  }

  public Interval(String duration){
    this.duration = Duration.parse(duration);
  }

  @Override
  public Instant next(Instant baseInstant, ZoneId zoneId) {
    LocalDateTime baseDate = LocalDateTime.ofInstant(baseInstant, zoneId);
    baseDate = baseDate.plus(duration);

    return baseDate.atZone(zoneId).toInstant();
  }

  @Override
  public void validateSchedule() {
    if (duration.getSeconds() < SMALLEST_SCHEDULE_IN_SECONDS) {
      throw new IllegalArgumentException(String.format("The smallest interval for recurring jobs is %d seconds. Please also make sure that your 'pollIntervalInSeconds' configuration matches the smallest recurring job interval.", SMALLEST_SCHEDULE_IN_SECONDS));
    }
  }

  @Override
  public String toString() {
    return duration.toString();
  }

  /**
   * Compares this object against the specified object. The result is {@code true}
   * if and only if the argument is not {@code null} and is a {@code Schedule}
   * object that whose seconds, minutes, hours, days, months, and days of
   * weeks sets are equal to those of this schedule.
   * <p>
   * The expression string used to create the schedule is not considered, as two
   * different expressions may produce same schedules.
   *
   * @param obj the object to compare with
   * @return {@code true} if the objects are the same; {@code false} otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Interval))
      return false;
    if (this == obj)
      return true;

    Interval interval = (Interval) obj;

    return this.duration.equals(interval.duration);
  }

  @Override
  public int hashCode() {
    return duration.hashCode();
  }

}

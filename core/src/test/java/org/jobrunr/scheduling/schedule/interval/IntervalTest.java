package org.jobrunr.scheduling.schedule.interval;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.*;
import java.time.format.*;
import java.util.stream.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

class IntervalTest {

  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final String ONE_SECOND = "PT1S";
  private static final String FORTY_EIGHT_HOURS = "PT48H";
  private static final String EIGHT_DAYS = "P8D";

  @ParameterizedTest
  @MethodSource("startInstantDurationAndResultInstant")
  void testInterval(String baseDate, String duration, String expectedResult) {
    try {
      Instant inputInstant = LocalDateTime.parse(baseDate, dateTimeFormatter).toInstant(UTC);
      Interval interval = new Interval(duration);
      Instant actualInstant = interval.next(inputInstant, UTC);
      Instant expectedInstant = LocalDateTime.parse(expectedResult, dateTimeFormatter).toInstant(UTC);

      assertThat(actualInstant)
          .describedAs("Expecting %s to be equal to %s for duration %s and start date %s", actualInstant, expectedInstant, duration, inputInstant)
          .isEqualTo(expectedInstant);
    } catch (Exception e) {
      System.out.println(String.format("Error for %s, %s and %s", baseDate, duration, expectedResult));
      throw e;
    }
  }

  @Test
  void intervalsAreScheduledIndependentlyOfZoneId() {
    int hour = 8;
    Instant now = Instant.now();

    Instant actualNextInstant1 = new Interval(Duration.ofHours(hour)).next(now, ZoneId.of("+02:00"));
    Instant actualNextInstant2 = new Interval(Duration.ofHours(hour)).next(now, UTC);

    assertThat(actualNextInstant1).isEqualTo(actualNextInstant2);
  }

  @Test
  void intervalsAreEqual() {
    Interval interval1 = new Interval(Duration.ofDays(1));
    Interval interval2 = new Interval(Duration.ofHours(24));

    assertThat(interval1)
            .isEqualTo(interval2)
            .hasSameHashCodeAs(interval2);
  }

  @Test
  void intervalsCanBeCompared() {
    LocalDateTime now = LocalDateTime.now();

    Interval interval1 = new Interval(Duration.ofHours(23));
    Interval interval2 = new Interval(Duration.ofDays(1));

    assertThat(interval1)
            .describedAs("Expecting %s to be less than %s. Current LocalDateTime", interval1.next().toString(), interval2.next().toString(), now.toString())
            .isLessThan(interval2);
  }

  static Stream<Arguments> startInstantDurationAndResultInstant() {
    return Stream.of(
        arguments("2019-01-01 00:00:00", ONE_SECOND, "2019-01-01 00:00:01"),
        arguments("2019-01-01 00:00:09", ONE_SECOND, "2019-01-01 00:00:10"),

        // Second rollover
        arguments("2019-01-01 00:58:59", ONE_SECOND, "2019-01-01 00:59:00"),
        arguments("2019-01-01 11:59:59", ONE_SECOND, "2019-01-01 12:00:00"),
        // Minute rollover
        arguments("2019-01-01 00:59:59", ONE_SECOND, "2019-01-01 01:00:00"),
        arguments("2019-01-01 11:59:59", ONE_SECOND, "2019-01-01 12:00:00"),
        // Hour rollover
        arguments("2019-01-01 23:59:59", ONE_SECOND, "2019-01-02 00:00:00"),
        // Month rollover
        arguments("2021-11-29 23:59:59", ONE_SECOND, "2021-11-30 00:00:00"),
        arguments("2019-02-28 23:59:59", ONE_SECOND, "2019-03-01 00:00:00"),
        // Year rollover
        arguments("2019-12-31 23:59:59", ONE_SECOND, "2020-01-01 00:00:00"),
        // Leap year
        arguments("2020-02-28 23:59:59", ONE_SECOND, "2020-02-29 00:00:00"),

        arguments("2021-01-01 11:59:59", FORTY_EIGHT_HOURS, "2021-01-03 11:59:59"),
        arguments("2021-11-29 11:59:59", FORTY_EIGHT_HOURS, "2021-12-01 11:59:59"),

        arguments("2021-01-01 11:59:59", EIGHT_DAYS, "2021-01-09 11:59:59"),
        arguments("2021-11-29 11:59:59", EIGHT_DAYS, "2021-12-07 11:59:59")
    );
  }
}

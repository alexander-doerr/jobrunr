package org.jobrunr.scheduling.schedule.interval;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.micrometer.core.instrument.binder.httpcomponents.*;
import java.time.*;
import java.time.format.*;
import java.util.stream.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

class IntervalTest {

  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final String TEN_SECONDS = "PT1S";
  private static final String FORTY_EIGHT_HOURS = "PT48H";
  private static final String EIGHT_DAYS = "P8D";

  @ParameterizedTest
  @MethodSource("startInstantDurationAndResultInstant")
  void testInterval(String baseDate, String durationExpression) {
    try {
      Instant inputInstant = LocalDateTime.parse(baseDate, dateTimeFormatter).toInstant(UTC);
      Interval interval = new Interval(durationExpression);
      Duration duration = Duration.parse(durationExpression);
      Instant actualInstant = interval.next(inputInstant, UTC);
      Instant now = Instant.now();

      assertThat(actualInstant)
          .describedAs("Expecting %s to be after or equal to %s for duration %s and start date %s", actualInstant, now, duration, inputInstant)
          .isAfterOrEqualTo(now);
      assertThat(actualInstant)
          .describedAs("Expecting %s to be before to %s for duration %s and start date %s", actualInstant, now.plus(duration), duration, inputInstant)
          .isBefore(now.plus(duration));
    } catch (Exception e) {
      System.out.println(String.format("Error for %s and %s", baseDate, durationExpression));
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
        arguments("2019-01-01 00:00:00", TEN_SECONDS),
        arguments("2019-01-01 00:00:09", TEN_SECONDS),
        arguments("2019-01-01 00:58:59", TEN_SECONDS),
        arguments("2019-01-01 11:59:59", TEN_SECONDS),
        arguments("2019-01-01 00:59:59", TEN_SECONDS),
        arguments("2019-01-01 11:59:59", TEN_SECONDS),
        arguments("2019-01-01 23:59:59", TEN_SECONDS),
        arguments("2021-11-29 23:59:59", TEN_SECONDS),
        arguments("2019-02-28 23:59:59", TEN_SECONDS),
        arguments("2019-12-31 23:59:59", TEN_SECONDS),
        arguments("2020-02-28 23:59:59", TEN_SECONDS),

        arguments("2021-01-01 11:59:59", FORTY_EIGHT_HOURS),
        arguments("2021-11-29 11:59:59", FORTY_EIGHT_HOURS),
        arguments("2021-11-28 11:59:59", FORTY_EIGHT_HOURS),

        arguments("2021-01-01 11:59:59", EIGHT_DAYS),
        arguments("2021-11-29 11:59:59", EIGHT_DAYS),
        arguments("2021-11-28 11:59:59", EIGHT_DAYS)
    );
  }
}

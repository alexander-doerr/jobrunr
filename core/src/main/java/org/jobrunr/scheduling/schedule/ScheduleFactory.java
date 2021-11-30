package org.jobrunr.scheduling.schedule;

import org.jobrunr.scheduling.schedule.cron.*;
import org.jobrunr.scheduling.schedule.interval.*;

public class ScheduleFactory {
  public static Schedule getSchedule(String schedule){

    if(schedule.matches("\\s+")){
      return CronExpression.create(schedule);
    }
    else {
      return new Interval(schedule);
    }
  }
}

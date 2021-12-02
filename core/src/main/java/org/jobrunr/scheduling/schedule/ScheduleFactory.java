package org.jobrunr.scheduling.schedule;

import org.jobrunr.scheduling.schedule.cron.*;
import org.jobrunr.scheduling.schedule.interval.*;

public class ScheduleFactory {
  public static Schedule getSchedule(String scheduleExpression){
    ScheduleExpressionType type = ScheduleExpressionType.getScheduleType(scheduleExpression);

    if(type.equals(ScheduleExpressionType.CRON_EXPRESSION)){
      return CronExpression.create(scheduleExpression);
    }
    else if(type.equals(ScheduleExpressionType.INTERVAL)){
      return new Interval(scheduleExpression);
    }
    else {
      throw new InvalidScheduleException("Schedule expression cannot be mapped to any type");
    }
  }
}

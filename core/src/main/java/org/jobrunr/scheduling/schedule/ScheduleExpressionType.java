package org.jobrunr.scheduling.schedule;

public enum ScheduleExpressionType {
  CRON_EXPRESSION,
  INTERVAL,
  INVALID,
  UNKNOWN;

  public static final ScheduleExpressionType getScheduleType(String scheduleExpression){
    if(scheduleExpression == null || scheduleExpression.isEmpty()) {
      return INVALID;
    }
    else if (scheduleExpression.matches(".*\\s.*")){
      return CRON_EXPRESSION;
    }
    else if (scheduleExpression.startsWith("P")){
      return INTERVAL;
    }

    return UNKNOWN;
  }
}

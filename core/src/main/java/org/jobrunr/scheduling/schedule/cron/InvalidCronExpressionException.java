package org.jobrunr.scheduling.schedule.cron;

public class InvalidCronExpressionException extends RuntimeException {

    InvalidCronExpressionException(String message) {
        super(message);
    }

    InvalidCronExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

}

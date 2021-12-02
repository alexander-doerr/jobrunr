package org.jobrunr.jobs;

import org.jobrunr.jobs.states.EnqueuedState;
import org.jobrunr.jobs.states.ScheduledState;
import org.jobrunr.scheduling.schedule.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.jobrunr.scheduling.schedule.interval.*;

public class RecurringJob extends AbstractJob {

    private String id;
    private String scheduleExpression;
    private String zoneId;
    private Instant createdAt;

    private RecurringJob() {
        // used for deserialization
    }

    public RecurringJob(String id, JobDetails jobDetails, Schedule schedule, ZoneId zoneId) {
        this(id, jobDetails, schedule.toString(), zoneId.getId());
    }

    public RecurringJob(String id, JobDetails jobDetails, String expression, String zoneId) {
        super(jobDetails);
        this.id = validateAndSetId(id);
        this.scheduleExpression = expression;
        this.zoneId = zoneId;
        this.createdAt = Instant.now();
        ScheduleFactory.getSchedule(expression).validateSchedule();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getScheduleExpression() {
        return scheduleExpression;
    }

    public Job toScheduledJob() {
        Instant nextRun = getNextRun();
        final Job job = new Job(getJobDetails(), new ScheduledState(nextRun, this));
        job.setJobName(getJobName());
        return job;
    }

    public Job toEnqueuedJob() {
        final Job job = new Job(getJobDetails(), new EnqueuedState());
        job.setJobName(getJobName());
        return job;
    }

    public String getZoneId() {
        return zoneId;
    }

    public Instant getNextRun() {
        Schedule schedule = ScheduleFactory.getSchedule(scheduleExpression);
        return getNextRun(schedule);
    }

    private Instant getNextRun(Schedule schedule) {
        if(schedule instanceof Interval){
            return schedule.next(createdAt, ZoneId.of(zoneId));
        }

        return schedule.next(ZoneId.of(zoneId));
    }

    private String validateAndSetId(String input) {
        String result = Optional.ofNullable(input).orElse(getJobSignature().replace("$", "_")); //why: to support inner classes

        if (!result.matches("[\\dA-Za-z-_(),.]+")) {
            throw new IllegalArgumentException("The id of a recurring job can only contain letters and numbers.");
        }
        return result;
    }

    @Override
    public String toString() {
        return "RecurringJob{" +
                "id=" + id +
                ", version='" + getVersion() + '\'' +
                ", identity='" + System.identityHashCode(this) + '\'' +
                ", jobSignature='" + getJobSignature() + '\'' +
                ", jobName='" + getJobName() + '\'' +
                '}';
    }
}

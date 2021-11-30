package org.jobrunr.jobs;

import org.jobrunr.jobs.states.EnqueuedState;
import org.jobrunr.jobs.states.ScheduledState;
import org.jobrunr.scheduling.schedule.*;
import org.jobrunr.scheduling.schedule.cron.CronExpression;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

public class RecurringJob extends AbstractJob {

    private String id;
    private Schedule schedule;
    private String zoneId;

    private RecurringJob() {
        // used for deserialization
    }

    public RecurringJob(String id, JobDetails jobDetails, Schedule schedule, ZoneId zoneId) {
        super(jobDetails);
        this.id = validateAndSetId(id);
        this.schedule = schedule;
        this.zoneId = zoneId.getId();

        this.schedule.validateSchedule();
    }

    public RecurringJob(String id, JobDetails jobDetails, String schedule, String zoneId) {
        this(id, jobDetails, ScheduleFactory.getSchedule(schedule), ZoneId.of(zoneId));
    }

    @Override
    public String getId() {
        return id;
    }

    public Schedule getSchedule() {
        return schedule;
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

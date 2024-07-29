package it.uniroma2.pmcsn.parks.verification;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.queue.AttractionQueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;

public class VerificationAttraction extends StatsCenter {

    private double popularity;
    private double avgDuration;

    public VerificationAttraction(String name, int numberOfSeats, double popularity, double avgDuration) {

        super(name, new AttractionQueueManager(), numberOfSeats);
        this.popularity = popularity;
        this.avgDuration = avgDuration;
    }

    @Override
    protected void doArrival(RiderGroup job) {
        if (job.getGroupSize() != 1) {
            throw new RuntimeException("Verification happens with ");
        }
    }

    @Override
    protected void doEndService(RiderGroup endedJob) {
    }

    @Override
    protected void collectEndServiceStats(RiderGroup endedJob) {
        double jobServiceTime = this.getServiceTime(endedJob);

        this.stats.addServiceTime(jobServiceTime);

        this.stats.addServedGroup(endedJob.getGroupSize());
    }

    @Override
    protected void terminateService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);

        this.startService();
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        int freeSlots = slotNumber - currentServingJobs.size();
        return queueManager.extractFromQueues(freeSlots);
    }

    @Override
    protected Double getNewServiceTime(RiderGroup job) {
        return RandomHandler.getInstance().getUniform(name, avgDuration - 0.5,
                avgDuration + 0.5);
    }

    @Override
    protected boolean canServe(Integer jobSize) {
        return slotNumber - currentServingJobs.size() >= 1;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public double getAvgDuration() {
        return this.avgDuration;
    }

}
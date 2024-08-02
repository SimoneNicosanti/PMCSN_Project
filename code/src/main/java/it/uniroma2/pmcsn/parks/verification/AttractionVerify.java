package it.uniroma2.pmcsn.parks.verification;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.Attraction;

//*  With repsect to the "Attraction" center, the "VerificationAttraction" center behaves as a normal multiserver with two different priority queues. For consistency, during the verification phase jobs that arrive to the attraction are expected to have a single item. The implementation assumes the job size is always 1, in fact an exception is thrown otherwise when the job arrives to the attraction. */

public class AttractionVerify extends Attraction {

    public AttractionVerify(String name, int numberOfSeats, double popularity, double avgDuration) {
        super(name, numberOfSeats, popularity, avgDuration);
    }

    @Override
    public void arrival(RiderGroup job) {
        if (job.getGroupSize() != 1) {
            throw new RuntimeException("Verification takes place with a single rider per group");
        }

        this.manageArrival(job);
    }

    @Override
    protected void collectEndServiceStats(RiderGroup endedJob) {
        double jobServiceTime = this.retrieveServiceTime(endedJob);

        endedJob.getGroupStats().incrementRidesInfo(this.getName(), jobServiceTime);

        this.stats.addServiceTime(jobServiceTime);

        this.stats.endServiceUpdate(jobServiceTime, endedJob.getGroupSize());

        this.serviceBatchStats.addTime(jobServiceTime);
    }

    @Override
    public void endService(RiderGroup endedJob) {
        this.currentServingJobs.remove(endedJob);
        this.startService();

        this.manageEndService(endedJob);
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        int freeSlots = slotNumber - currentServingJobs.size();
        return queueManager.extractFromQueues(freeSlots);
    }

    @Override
    protected Double getNewServiceTime(RiderGroup job) {
        return RandomHandler.getInstance().getExponential(name, avgServiceTime);
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return slotNumber - currentServingJobs.size() >= 1;
    }

}
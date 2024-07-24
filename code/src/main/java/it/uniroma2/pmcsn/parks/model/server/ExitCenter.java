package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

//** Fake center for exiting jobs */
public class ExitCenter extends Center {

    public ExitCenter(String name) {
        super(name, null, null);
    }

    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return true;
    }

    @Override
    public void arrival(RiderGroup job) {
        // TODO save job stats
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return true;
    }

    @Override
    protected void startService() {
    }

    @Override
    public void endService(RiderGroup endedJob) {
    }

    @Override
    protected List<RiderGroup> getJobsToServe() {
        return null;
    }

    @Override
    protected Double getNewServiceTime(RiderGroup group) {
        return null;
    }

    @Override
    protected void terminateService(RiderGroup endedJob) {
    }

}

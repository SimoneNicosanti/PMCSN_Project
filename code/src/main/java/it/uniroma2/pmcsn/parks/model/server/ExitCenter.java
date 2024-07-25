package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.utils.RiderStatisticsWriter;

//** Fake center for exiting jobs */
public class ExitCenter extends Center {

    RiderStatisticsWriter writer;

    public ExitCenter(String name, RiderStatisticsWriter writer) {
        super(name, null, null);
        this.writer = writer;
    }

    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return true;
    }

    @Override
    public void arrival(RiderGroup job) {
        // TODO save job stats
        writer.writeStatistics(job);
        EventLogger.logExit(ClockHandler.getInstance().getClock());
    }

    @Override
    public boolean canServe(Integer jobSize) {
        return true;
    }

    @Override
    public List<RiderGroup> startService() {
        return null;
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

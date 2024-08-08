package it.uniroma2.pmcsn.parks.model.server.concrete_servers;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;
import it.uniroma2.pmcsn.parks.utils.StatisticsWriter;

/**
 * Fake center for exiting jobs
 * It simply writes stas on a file when the job exists from the system
 */
public class ExitCenter implements Center<RiderGroup> {
    String name;

    public ExitCenter(String name) {
        this.name = name;
    }

    @Override
    public QueuePriority arrival(RiderGroup job) {
        String filename = Constants.JOB_STATS_FILENAME;
        if (!Constants.VERIFICATION_MODE) {
            StatisticsWriter.writeStatistics("Job", filename, job);
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return false;
    }

    @Override
    public void endService(RiderGroup endedJob) {
    }

    @Override
    public void setNextRoutingNode(RoutingNode<RiderGroup> nextRoutingNode) {
    }

    @Override
    public List<RiderGroup> startService() {
        return null;
    }

    @Override
    public Integer getQueueLenght(GroupPriority prio) {
        return 0;
    }

    @Override
    public Double getPopularity() {
        return 0.0;
    }

    @Override
    public boolean canServe(Integer slots) {
        return true;
    }

    @Override
    public List<RiderGroup> closeCenter() {
        return null;
    }

}

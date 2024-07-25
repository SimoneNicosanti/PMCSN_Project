package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.interfaces.RoutingNode;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.utils.RiderStatisticsWriter;

//** Fake center for exiting jobs */
public class ExitCenter implements CenterInterface<RiderGroup> {
    String name;

    public ExitCenter(String name) {
        this.name = name;
    }

    @Override
    public void arrival(RiderGroup job) {
        // TODO change filename for the different configuration
        String filename = Config.JOB_STATS_FILENAME;
        RiderStatisticsWriter.writeStatistics(filename, job);
        EventLogger.logExit(ClockHandler.getInstance().getClock());
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

}

package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;
import it.uniroma2.pmcsn.parks.verification.ValidationWriter;

public class ConsistencyChecksController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        long start = System.currentTimeMillis();
        new ConsistencyChecksController().simulate();
        Long time = System.currentTimeMillis() - start;
        System.out.println("Run Time >>> " + time);
    }

    public ConsistencyChecksController() {
        Constants.MODE = SimulationMode.CONSISTENCY_CHECK;
        Constants.VERIFICATION_BATCH_NUMBER = 50;
        Constants.VERIFICATION_BATCH_SIZE = 1024;

        this.networkBuilder = new NetworkBuilder();
    }

    @Override
    public void simulate() {

        ValidationWriter.resetConsistencyCheckDirectory();

        // Base Arrival Rate
        Constants.CONSISTENCY_CHECKS_CONFIG_FILENAME = Constants.PRE_CONSISTENCY_CHECKS_CONFIG_FILENAME;
        simulateOnce(0);
        printUsedStrams();

        resetSingletons();

        // Increase the arrival rate
        Constants.CONSISTENCY_CHECKS_CONFIG_FILENAME = Constants.POST_CONSISTENCY_CHECKS_CONFIG_FILENAME;
        simulateOnce(1);
        printUsedStrams();

    }

    @SuppressWarnings("unused")
    private void printUsedStrams() {
        RandomHandler.getInstance().getStreamMap()
                .forEach(
                        (name, streamIdx) -> System.out.println("Name >>> " + name + " - Stream Idx >>> " + streamIdx));
        ;

    }

    private void resetSingletons() {
        ClockHandler.reset();
        ConfigHandler.reset();
        EventsPool.reset();
        RandomHandler.reset();
    }

    public void simulateOnce(int i) {

        this.networkBuilder.buildNetwork();

        // Reset verification stats
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.getInstance().scheduleNewEvent(arrivalEvent);

        List<Center<RiderGroup>> centerList = batchSimulation();

        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();
        computer.updateAllStatistics(centerList);

        List<ConfidenceInterval> confidenceIntervals = new ArrayList<>();
        for (Center<RiderGroup> center : centerList) {
            BatchStats queueBatchStats = ((StatsCenter) center).getQueueBatchStats();
            ConfidenceInterval confidenceInterval = ConfidenceIntervalComputer
                    .computeConfidenceInterval(queueBatchStats.getNumberAvgs(), center.getName(), "E[Tq]");
            confidenceIntervals.add(confidenceInterval);
        }
        // List<ConfidenceInterval> confidenceIntervals =
        // computer.computeAllConfidenceIntervals();

        ValidationWriter.writeConfidenceIntervals(confidenceIntervals, "ConfidenceIntervals_" + i);
    }

    public List<Center<RiderGroup>> batchSimulation() {
        int i = 0;

        while (!stopSimulation()) {

            SystemEvent nextEvent = EventsPool.getInstance().getNextEvent();
            Double nextEventTime = nextEvent.getEventTime();

            ClockHandler.getInstance().setClock(nextEventTime);
            RiderGroup job = nextEvent.getJob();
            Center<RiderGroup> center = nextEvent.getEventCenter();
            switch (nextEvent.getEventType()) {
                case ARRIVAL:
                    boolean mustServe = center.isQueueEmptyAndCanServe(job.getGroupSize());
                    center.arrival(job);
                    if (mustServe) {
                        center.startService();
                    }
                    break;

                case END_PROCESS:
                    center.endService(job);
                    if (center.canServe(1)) {
                        center.startService();
                    }
                    break;
            }

            int dividend = (int) ClockHandler.getInstance().getClock();

            if (dividend / 10000 != i) {
                System.out.println(ClockHandler.getInstance().getClock());
                i++;
            }

        }

        System.out.println("Final Clock >>> " + ClockHandler.getInstance().getClock());

        return networkBuilder.getAllCenters();
    }

    private boolean stopSimulation() {
        for (Center<RiderGroup> center : this.networkBuilder.getAllCenters()) {
            StatsCenter statCenter = (StatsCenter) center;

            if (!statCenter.areBatchesCompleted()) {
                return false;
            }
        }
        return true;
    }
}
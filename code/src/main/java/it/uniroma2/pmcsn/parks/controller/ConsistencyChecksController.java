package it.uniroma2.pmcsn.parks.controller;

import java.util.List;

import it.uniroma2.pmcsn.parks.SimulationMode;
import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.utils.WriterHelper;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer.ConfidenceInterval;
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
        this.networkBuilder.buildNetwork();

        // this.configHandler = ConfigHandler.getInstance();

        // this.init_simulation();

        // this.currentInterval = configHandler.getCurrentInterval();
    }

    @Override
    public void simulate() {

        ValidationWriter.resetData();

        // Reset verification stats
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);

        List<Center<RiderGroup>> centerList = batchSimulation();

        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();
        computer.updateStatistics(centerList);
        List<ConfidenceInterval> confidenceIntervals = computer.computeConfidenceIntervals();

        ValidationWriter.writeConfidenceIntervals(confidenceIntervals, "ConfidenceIntervals");
    }

    public List<Center<RiderGroup>> batchSimulation() {
        int i = 0;

        while (!stopSimulation()) {

            // long time = System.currentTimeMillis();

            SystemEvent<RiderGroup> nextEvent = EventsPool.<RiderGroup>getInstance().getNextEvent();
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

            // time = System.currentTimeMillis() - time;
            // System.out.println(time);

            int dividend = (int) ClockHandler.getInstance().getClock();

            if (dividend / 10000 != i) {
                System.out.println(ClockHandler.getInstance().getClock());
                i++;
            }

        }

        System.out.println("Final Clock >>> " + ClockHandler.getInstance().getClock());

        EventLogger.logRandomStreams("RandomStreams");

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
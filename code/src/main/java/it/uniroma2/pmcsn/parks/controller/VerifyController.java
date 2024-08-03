package it.uniroma2.pmcsn.parks.controller;

import java.util.List;
import java.util.Map;

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
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.verification.TheoreticalValueComputer;
import it.uniroma2.pmcsn.parks.verification.VerificationWriter;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer.ConfidenceInterval;

public class VerifyController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;

    public VerifyController() {
        Constants.VERIFICATION_MODE = true;
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();

        // this.configHandler = ConfigHandler.getInstance();

        // this.init_simulation();

        // this.currentInterval = configHandler.getCurrentInterval();
    }

    @Override
    public void simulate() {

        VerificationWriter.resetData();

        // Reset verification stats
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);

        List<Center<RiderGroup>> centerList = batchSimulation();

        TheoreticalValueComputer theoryValueComputer = new TheoreticalValueComputer();
        Map<String, Double> theoryMap = theoryValueComputer.computeTheoreticalQueueTimeMap(centerList);
        VerificationWriter.writeTheoreticalQueueTimeValues(theoryMap);

        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();
        computer.updateStatistics(centerList);
        List<ConfidenceInterval> confidenceIntervals = computer.computeConfidenceIntervals();
        VerificationWriter.writeConfidenceIntervals(confidenceIntervals, "ConfidenceIntervals");

        // Write confidence intervals for all statistics
    }

    public List<Center<RiderGroup>> batchSimulation() {

        while (!stopSimulation()) {

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

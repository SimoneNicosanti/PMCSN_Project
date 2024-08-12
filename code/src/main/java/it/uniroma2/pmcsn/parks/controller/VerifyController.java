package it.uniroma2.pmcsn.parks.controller;

import java.util.List;
import java.util.Map;

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
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.utils.TheoreticalValueComputer;
import it.uniroma2.pmcsn.parks.utils.ConfidenceIntervalComputer.ConfidenceInterval;
import it.uniroma2.pmcsn.parks.verification.VerificationWriter;
import it.uniroma2.pmcsn.parks.writers.EventLogger;
import it.uniroma2.pmcsn.parks.writers.WriterHelper;

public class VerifyController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;

    public static void main(String[] args) {
        WriterHelper.createAllFolders();
        long start = System.currentTimeMillis();
        new VerifyController().simulate();
        Long time = System.currentTimeMillis() - start;
        System.out.println("Run Time >>> " + time);
    }

    public VerifyController() {
        Constants.MODE = SimulationMode.VERIFICATION;
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
        SystemEvent arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.getInstance().scheduleNewEvent(arrivalEvent);

        List<Center<RiderGroup>> centerList = batchSimulation();

        TheoreticalValueComputer theoryValueComputer = new TheoreticalValueComputer();
        Map<String, Map<String, Double>> theoryMap = theoryValueComputer.computeAllTheoreticalValues(centerList);
        // VerificationWriter.writeTheoreticalQueueTimeValues(theoryMap);

        VerificationWriter.writeSimulationResult(centerList, theoryMap);

        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();
        computer.updateAllStatistics(centerList);
        List<ConfidenceInterval> confidenceIntervals = computer.computeAllConfidenceIntervals();
        VerificationWriter.writeConfidenceIntervals(confidenceIntervals, theoryMap,
                "ConfidenceIntervals");

        // Write confidence intervals for all statistics
    }

    public List<Center<RiderGroup>> batchSimulation() {

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

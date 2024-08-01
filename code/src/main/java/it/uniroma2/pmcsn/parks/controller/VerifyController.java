package it.uniroma2.pmcsn.parks.controller;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer;
import it.uniroma2.pmcsn.parks.verification.VerificationWriter;
import it.uniroma2.pmcsn.parks.verification.ConfidenceIntervalComputer.ConfidenceInterval;

public class VerifyController implements Controller<RiderGroup> {

    public VerifyController() {

        // this.configHandler = ConfigHandler.getInstance();

        // this.init_simulation();

        // this.currentInterval = configHandler.getCurrentInterval();
    }

    @Override
    public void simulate() {

        VerificationWriter.resetData();

        Constants.VERIFICATION_MODE = true;
        ConfidenceIntervalComputer computer = new ConfidenceIntervalComputer();

        // Reset verification stats

        for (int i = 0; i < Constants.VERIFY_SIMULATION_NUM; i++) {
            System.out.println("Simulation " + i);
            List<Center<RiderGroup>> centerList = simulateOnce();
            computer.updateStatistics(centerList);

            VerificationWriter.writeAllVerificationStatistics(Constants.VERIFICATION_PATH, "CenterStats", centerList);

            ClockHandler.getInstance().setClock(0.0);
            EventsPool.getInstance().resetPool(); // Removing not handled events

        }

        List<ConfidenceInterval> confidenceIntervals = computer.computeConfidenceIntervals();
        VerificationWriter.writeConfidenceIntervals(confidenceIntervals, "ConfidenceIntervals");

        // Write confidence intervals for all statistics
    }

    public List<Center<RiderGroup>> simulateOnce() {

        NetworkBuilder networkBuilder = new NetworkBuilder();
        networkBuilder.buildNetwork();

        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);

        Double endClock = ConfigHandler.getInstance().getCurrentInterval().getEnd();

        while (true) {

            SystemEvent<RiderGroup> nextEvent = EventsPool.<RiderGroup>getInstance().getNextEvent();
            Double nextEventTime = nextEvent.getEventTime();
            if (nextEventTime > endClock) {
                break;
            }

            ClockHandler.getInstance().setClock(nextEventTime);
            RiderGroup job = nextEvent.getJob();
            Center<RiderGroup> center = nextEvent.getEventCenter();
            switch (nextEvent.getEventType()) {
                case ARRIVAL:
                    center.arrival(job);
                    break;

                case END_PROCESS:
                    center.endService(job);
                    break;
            }
        }

        EventLogger.logRandomStreams("RandomStreams");

        return networkBuilder.getAllCenters();
    }

}

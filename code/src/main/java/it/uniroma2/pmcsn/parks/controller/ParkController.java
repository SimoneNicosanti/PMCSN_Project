package it.uniroma2.pmcsn.parks.controller;

import java.nio.file.Path;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.event.SystemEvent;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.ExitCenter;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.utils.StatisticsWriter;
import it.uniroma2.pmcsn.parks.verification.VerificationWriter;

public class ParkController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;
    private ClockHandler clockHandler;
    private ConfigHandler configHandler;
    private Interval currentInterval;

    public ParkController() {
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();
        this.configHandler = ConfigHandler.getInstance();

        this.init_simulation();

        this.currentInterval = configHandler.getCurrentInterval();
    }

    @Override
    public void startSimulation() {

        this.scheduleArrivalEvent();
        clockHandler = ClockHandler.getInstance();

        int lastDividend = 0;
        int divisor = 1440;

        while (true) {

            SystemEvent<RiderGroup> nextEvent = EventsPool.<RiderGroup>getInstance().getNextEvent();
            if (nextEvent == null) {
                // When all the events finish, the simulation ends
                break;
            }

            clockHandler.setClock(nextEvent.getEventTime());

            // TODO: handle this in a better way, it was just to test the verification
            // faster
            if (Constants.VERIFICATION_MODE) {
                int dividend = (int) nextEvent.getEventTime() / divisor;
                if (dividend > lastDividend) {
                    lastDividend = dividend;

                    for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {
                        VerificationWriter.writeVerificationStatistics("Verification", "TotalStatsCenter_" + dividend,
                                center);
                    }
                }
            }

            // Check if the current interval changed
            Interval interval = configHandler.getInterval(clockHandler.getClock());
            if (isIntervalChanged(interval)) {
                changeInterval(interval);
            }

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

        if (Constants.INTERVAL_STATS) {
            // write end stats
            writeCenterStats(currentInterval);
        } else {
            writeCenterStats(null);
        }

        EventLogger.logRandomStreams("RandomStreams");
    }

    private boolean isIntervalChanged(Interval interval) {
        return !interval.equals(this.currentInterval);
    }

    private void changeInterval(Interval interval) {
        System.out.println("CURRENT INTERVAL CHANGED");
        System.out.println(interval.getStart() + " - " + interval.getEnd());
        System.out.println("");

        if (Constants.INTERVAL_STATS) {
            // Save stats for the ended interval time
            writeCenterStats(currentInterval);
            // Reset stats for the next interval time
            resetCenterStats();
        }

        // Change the parameters based on the interval
        changeParameters(interval);
        this.currentInterval = interval;
    }

    private void changeParameters(Interval interval) {
        ConfigHandler.getInstance().changeParameters(interval);
    }

    private void writeCenterStats(Interval interval) {
        for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {

            if (interval == null) {
                StatisticsWriter.writeCenterStatistics(Path.of(".", "Center", "Total").toString(),
                        "TotalCenterStats", center);

                // Check whether we are veryfing the model or not
                if (Constants.VERIFICATION_MODE) {
                    VerificationWriter.writeVerificationStatistics("Verification", "TotalStatsCenter", center);
                }

            } else {
                StatisticsWriter.writeCenterStatistics(Path.of(".", "Center", "Interval").toString(),
                        interval.getStart() + "-" + interval.getEnd(),
                        center);

                if (Constants.VERIFICATION_MODE) {
                    VerificationWriter.writeVerificationStatistics("Verification",
                            interval.getStart() + "-" + interval.getEnd(), center);
                }

            }
        }
    }

    private void resetCenterStats() {
        for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {
            if (center instanceof ExitCenter)
                continue;
            ((StatsCenter) center).resetCenterStats();
        }
    }

    private void init_simulation() {
        // Reset statistics
        StatisticsWriter.resetStatistics("Job");

        if (Constants.INTERVAL_STATS) {
            StatisticsWriter.resetStatistics(Path.of(".", "Center", "Interval").toString());
        } else {
            StatisticsWriter.resetStatistics(Path.of(".", "Center", "Total").toString());
        }

        if (Constants.VERIFICATION_MODE) {
            StatisticsWriter.resetStatistics("Verification");
        }
        // Prepare the logger and set the system clock to 0
        EventLogger.prepareLog();
        ClockHandler.getInstance().setClock(0);
    }

    private void scheduleArrivalEvent() {
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        SystemEvent<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);
    }
}

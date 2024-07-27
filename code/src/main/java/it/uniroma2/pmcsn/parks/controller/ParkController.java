package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.StatsCenter;
import it.uniroma2.pmcsn.parks.utils.EventLogger;
import it.uniroma2.pmcsn.parks.utils.RiderStatisticsWriter;

public class ParkController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;
    private ClockHandler clockHandler;
    private Interval currentInterval = null;

    public ParkController() {
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();
    }

    @Override
    public void startSimulation() {

        this.init_simulation();

        clockHandler = ClockHandler.getInstance();
        currentInterval = clockHandler.getCurrentInterval();

        this.scheduleArrivalEvent();

        while (true) {
            Event<RiderGroup> nextEvent = EventsPool.<RiderGroup>getInstance().getNextEvent();
            if (nextEvent == null) {
                // When all the events finish, the simulation ends
                break;
            }

            clockHandler.setClock(nextEvent.getEventTime());
            // Check if the current interval changed
            this.checkInterval();

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

        // write end stats
        writeCenterStats(currentInterval);

    }

    private void checkInterval() {
        Interval interval = clockHandler.getInterval(clockHandler.getClock());

        if (!interval.equals(this.currentInterval)) {

            System.out.println("CURRENT INTERVAL CHANGED");
            System.out.println(interval.getStart() + " - " + interval.getEnd());
            System.out.println("");

            if (Constants.INTERVAL_STATS) {
                // Save stats for the ended interval time
                writeCenterStats(interval);
                // Reset stats for the next interval time
                resetCenterStats();
            }

            // Change the parameters based on the interval
            changeParameters(interval);
            this.currentInterval = interval;
        }
    }

    private void changeParameters(Interval interval) {
        ConfigHandler.getInstance().changeParameters(interval);
    }

    private void writeCenterStats(Interval interval) {
        for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {
            RiderStatisticsWriter.writeCenterStatistics("Center", interval.getStart() + "-" + interval.getEnd(),
                    center);
        }
    }

    private void resetCenterStats() {
        for (Center<RiderGroup> center : networkBuilder.getAllCenters()) {
            ((StatsCenter) center).resetCenterStats();
        }
    }

    private void init_simulation() {
        // Reset statistics
        RiderStatisticsWriter.resetStatistics("General");
        // Prepare the logger and set the system clock to 0
        EventLogger.prepareLog();
        ClockHandler.getInstance().setClock(0);
    }

    private void scheduleArrivalEvent() {
        Double arrivalRate = ConfigHandler.getInstance().getCurrentArrivalRate();
        // If arrivalRate == 0, stop the arrival
        if (arrivalRate == 0) {
            return;
        }
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Constants.ENTRANCE);
        Event<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);
    }
}

package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.Interval;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
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

        clockHandler = ClockHandler.getInstance();

        RiderStatisticsWriter.resetStatistics("General");
        this.init_simulation();
        this.scheduleArrivalEvent();

        // TODO Set termination condition
        int processedEventNumber = 0;
        while (processedEventNumber < 10_000) {
            Event<RiderGroup> nextEvent = EventsPool.<RiderGroup>getInstance().getNextEvent();
            if (nextEvent == null) {
                break;
            }

            // TODO Check if we are now in a new interval time of the day
            clockHandler.setClock(nextEvent.getEventTime());
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

            processedEventNumber++;
        }
    }

    private void checkInterval() {
        Interval interval = clockHandler.getInterval(clockHandler.getClock());

        if (!interval.equals(this.currentInterval)) {
            // TODO time interval changed -> change probabilities
            // TODO save stats for the ended interval
            this.currentInterval = interval;
        }
    }

    private void init_simulation() {
        // Prepare the logger and set the system clock to 0
        EventLogger.prepareLog();
        ClockHandler.getInstance().setClock(0);
    }

    private void scheduleArrivalEvent() {
        Center<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Config.ENTRANCE);
        Event<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);
    }

}

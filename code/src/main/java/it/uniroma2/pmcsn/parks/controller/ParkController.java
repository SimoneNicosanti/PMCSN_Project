package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Controller;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.EventsPool;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public class ParkController implements Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;

    public ParkController() {
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();
    }

    @Override
    public void startSimulation() {

        this.init_simulation();

        this.scheduleArrivalEvent();

        // TODO Set termination condition
        int processedEventNumber = 0;
        while (processedEventNumber < 300) {
            Event<RiderGroup> nextEvent = EventsPool.<RiderGroup>getInstance().getNextEvent();
            if (nextEvent == null) {
                continue;
            }
            ClockHandler.getInstance().setClock(nextEvent.getEventTime());

            RiderGroup job = nextEvent.getJob();
            CenterInterface<RiderGroup> center = nextEvent.getEventCenter();
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

    private void init_simulation() {
        // Prepare the logger and set the system clock to 0
        EventLogger.prepareLog();
        ClockHandler.getInstance().setClock(0);
    }

    private void scheduleArrivalEvent() {
        CenterInterface<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Config.ENTRANCE);
        Event<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        EventsPool.<RiderGroup>getInstance().scheduleNewEvent(arrivalEvent);
    }

}

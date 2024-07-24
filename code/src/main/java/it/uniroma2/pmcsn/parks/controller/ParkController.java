package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.factory.NetworkBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public class ParkController extends Controller<RiderGroup> {

    private NetworkBuilder networkBuilder;

    public ParkController() {
        this.networkBuilder = new NetworkBuilder();
        this.networkBuilder.buildNetwork();
    }

    @Override
    public void startSimulation() {
        EventLogger.prepareLog();

        this.init_clock();
        CenterInterface<RiderGroup> entranceCenter = networkBuilder.getCenterByName(Config.ENTRANCE);
        Event<RiderGroup> arrivalEvent = EventBuilder.getNewArrivalEvent(entranceCenter);
        this.eventsPool.scheduleNewEvent(arrivalEvent);

        // TODO Set termination condition
        int processedEventNumber = 0;
        while (processedEventNumber < 300) {
            Event<RiderGroup> nextEvent = this.eventsPool.getNextEvent();
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

}

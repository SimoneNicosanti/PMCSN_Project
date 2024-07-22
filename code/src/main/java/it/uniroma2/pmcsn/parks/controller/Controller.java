package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.CenterManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class Controller {

    private EventHandler<RiderGroup> eventHandler;
    private CenterManager<RiderGroup> centerManager;

    public Controller() {
        this.eventHandler = new EventHandler<>();
        this.centerManager = new CenterManager<>();
    }

    public void startSimulation() {
        // Initialize system clock
        ClockHandler.getInstance().setClock(0);

        // Schedule first arrival
        Center<RiderGroup> entranceCenter = centerManager.getCenterByName(Config.ENTRANCE);
        Event<RiderGroup> entranceEvent = new EventBuilder(EventType.ARRIVAL, entranceCenter)
                .buildEntranceArrivalEvent();

        eventHandler.scheduleNewEvent(entranceEvent);

        // TODO set end cycle condition
        while (true) {
            Event<RiderGroup> nextEvent = eventHandler.getNextEvent();

            switch (nextEvent.getEventType()) {
                case ARRIVAL:
                    // Add new job to the Entrance center
                    break;
                case START_PROCESS:
                    // Start service and schedule a new end_process event
                    break;
                case END_PROCESS:
                    // End the service and schedule a new start_process event if the queue is not
                    // empty
                    break;
            }

        }
    }
}

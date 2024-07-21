package it.uniroma2.pmcsn.parks.controller;

import it.uniroma2.pmcsn.parks.engineering.CenterManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class Controller {

    private EventHandler<RiderGroup> eventHandler;
    private CenterManager<RiderGroup> centerManager;

    public Controller() {
        this.eventHandler = new EventHandler<>();
        this.centerManager = new CenterManager<>();
    }


    public void startSimulation() {

        // Schedule first arrival
        eventHandler.scheduleNewArrival(centerManager.getCenterByName(Config.ENTRANCE), 0);

        // TODO set end cycle condition
        while(true) {
            Event<RiderGroup> nextEvent = eventHandler.getNextEvent();

            switch (nextEvent.getEventType()) {
                case ARRIVAL:
                    // Add new job to the Entrance center
                    break;
                case START_PROCESS:
                    // Start service and schedule a new end_process event
                    break;
                case END_PROCESS:
                    // End the service and schedule a new start_process event if the queue is not empty
                    break;
            }
            
            
        }
    }
}

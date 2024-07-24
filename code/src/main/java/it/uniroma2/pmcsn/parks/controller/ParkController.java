package it.uniroma2.pmcsn.parks.controller;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.utils.EventLogger;

public class ParkController extends Controller<RiderGroup> {

    private ParkEventProcessor eventProcessor;

    public ParkController() {
        this.eventProcessor = new ParkEventProcessor();
    }

    @Override
    public void startSimulation() {
        EventLogger.prepareLog();

        this.init_clock();
        Event<RiderGroup> arrivalEvent = eventProcessor.generateArrivalEvent();
        this.eventsPool.scheduleNewEvent(arrivalEvent);

        // TODO Set termination condition
        int processedEventNumber = 0;
        while (processedEventNumber < 100) {
            Event<RiderGroup> nexEvent = this.eventsPool.getNextEvent();
            if (nexEvent == null) {
                continue;
            }
            ClockHandler.getInstance().setClock(nexEvent.getEventTime());

            List<Event<RiderGroup>> newEventsList = this.eventProcessor.processEvent(nexEvent);

            this.eventsPool.scheduleNewEvents(newEventsList);

            processedEventNumber++;

        }
    }

}

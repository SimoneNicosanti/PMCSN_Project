package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class EventProcessor<T> {

    private int routingRandomStreamIdx;

    private double RESTAURANT_AREA_THR = 0.2;
    private double ATTRACTION_AREA_THR = 0.6;

    private AttractionRoutingComputer attractionRoutingComputer;

    public EventProcessor() {
        this.routingRandomStreamIdx = RandomHandler.getInstance().getNewStreamIndex();
        this.attractionRoutingComputer = new AttractionRoutingComputer(null);
        // TODO PASS A VALID LIST TO THIS
    }

    public List<Event<T>> processEvent(Event<T> event) {
        Center<T> center = event.getEventCenter();
        T job = event.getJob();
        List<Event<T>> nextEvents = null;
        switch (event.getEventType()) {
            case ARRIVAL:
                center.arrival(job);
                nextEvents = generateNextEventsFromArrival(event);
                break;

            case START_PROCESS:
                double serviceTime = center.startService();
                nextEvents = generateNextEventsFromStart(event, serviceTime);
                break;

            case END_PROCESS:
                List<T> completedJobList = center.endService();
                nextEvents = generateNextEventsFromEnd(event, completedJobList);
                break;
        }

        return nextEvents;
    }

    // I fixed the Generic issue, but I need to understand the logic you've
    // introduced beforehand, why create different branches and a list if you have a
    // single event in input ??? Can't you just return a single new event ??
    private List<Event<T>> generateNextEventsFromArrival(Event<T> event) {
        List<Event<T>> newEventList = new ArrayList<>();
        if (event.getEventCenter() instanceof Attraction) {
            Attraction attraction = (Attraction) event.getEventCenter();
            if (!attraction.isServing()) {
                Event<T> newEvent = EventBuilder.buildEventFrom(event, EventType.START_PROCESS);
                newEventList.add(newEvent);
            }
        }
        // if (event.getEventCenter() instanceof Entrance) {}
        // if (event.getEventCenter() instanceof Restaurant) {}

        return newEventList;
    }

    private List<Event<T>> generateNextEventsFromStart(Event<T> event, double serviceTime) {
        List<Event<T>> newEventList = new ArrayList<>();
        if (event.getEventCenter() instanceof Attraction) {
            Event<T> newEvent = EventBuilder.buildEventFrom(event, EventType.END_PROCESS);
            newEvent.addDistributionTime(serviceTime);
            newEventList.add(newEvent);
        }
        // if (event.getEventCenter() instanceof Entrance) {}
        // if (event.getEventCenter() instanceof Restaurant) {}

        return newEventList;

    }

    private List<Event<RiderGroup>> generateNextEventsFromEnd(Event<T> event, List<T> completedJobList) {
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        if (event.getEventCenter() instanceof Attraction) {
            for (T completedJob : completedJobList) {
                double areaProbability = RandomHandler.getInstance().getRandom(routingRandomStreamIdx);

                if (areaProbability <= RESTAURANT_AREA_THR) {
                    // GO TO RESTAURANTS
                    // Schedule Arrival in restaurant
                } else if (areaProbability <= RESTAURANT_AREA_THR + ATTRACTION_AREA_THR) {
                    // GO TO OTHER ATTRACTIONS
                    // Schedule Arrival in attraction
                    /*
                     * Need to know for each attraction:
                     * - number of visits by a job per each attraction
                     * - popularity of attraction
                     * - (number of people in queue in that attraction)
                     * 
                     */
                    Attraction nextAttraction = attractionRoutingComputer
                            .computeNextAttractionForJob((RiderGroup) completedJob);
                    Event<RiderGroup> newEvent = new Event<RiderGroup>(nextAttraction, EventType.ARRIVAL,
                            ClockHandler.getInstance().getClock(), (RiderGroup) completedJob);
                    newEventList.add(newEvent);

                } else {
                    // EXIT FROM SYSTEM
                    // Take stats
                    // Find a way to manage
                }

                /*
                 * 1. Extract random number --> Selecting random stream
                 * 2. Check random value number --> check where it goes according to thr
                 * 3. If goes to other attractions
                 * 4. Compute probability for each attraction
                 * 5. Generate arrival events for those attraction
                 */
            }

        }
        // if (event.getEventCenter() instanceof Entrance) {}
        // if (event.getEventCenter() instanceof Restaurant) {}

        return newEventList;
    }

    /*
     * if (center is attraction)
     * if arrival and queues empty --> schedule next service
     * if start --> schedule end with returned service time
     * if end --> schedule start with same current time (start and end are the same)
     * and schedule arrival to other
     * attractions or exit from system
     * 
     * if (center is entrance)
     * if arrival and queues empty --> schedule next service
     * if start --> schedule end with returned service time
     * if end --> schedule arrival to other centers
     * 
     * if (center is restaurant)
     * if arrival and queues empty --> schedule next service
     * if start --> schedule end
     * if end --> schedule arrival to other centers
     */

}

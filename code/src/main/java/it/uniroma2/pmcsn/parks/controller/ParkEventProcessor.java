package it.uniroma2.pmcsn.parks.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import it.uniroma2.pmcsn.parks.engineering.CentersManager;
import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.factory.EventBuilder;
import it.uniroma2.pmcsn.parks.engineering.interfaces.EventProcessor;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.job.ServingGroup;
import it.uniroma2.pmcsn.parks.model.routing.AttractionRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.NetworkRoutingNode;
import it.uniroma2.pmcsn.parks.model.routing.RestaurantRoutingNode;
import it.uniroma2.pmcsn.parks.model.server.Attraction;
import it.uniroma2.pmcsn.parks.model.server.Center;
import it.uniroma2.pmcsn.parks.model.server.Entrance;
import it.uniroma2.pmcsn.parks.model.server.Restaurant;
import it.uniroma2.pmcsn.parks.utils.TestingUtils;

public class ParkEventProcessor implements EventProcessor<RiderGroup> {

    private CentersManager<RiderGroup> centersManager;
    private NetworkRoutingNode networkRoutingNode;

    public ParkEventProcessor() {
        this.centersManager = new CentersManager<>();

        // Just for testing, delete once the system is live
        List<Center<RiderGroup>> centers = TestingUtils.createTestingCentersList();

        this.centersManager.addCenterList(centers);

        // Create the Network Routing Node
        List<Attraction> attractions = this.centersManager.getAttractions();
        List<Restaurant> restaurants = this.centersManager.getRestaurants();
        this.networkRoutingNode = new NetworkRoutingNode(new AttractionRoutingNode(attractions),
                new RestaurantRoutingNode(restaurants));
    }

    @Override
    public List<Event<RiderGroup>> processEvent(Event<RiderGroup> event) {
        Center<RiderGroup> center = event.getEventCenter();
        RiderGroup job = event.getJob();
        List<Event<RiderGroup>> nextEvents = new ArrayList<>();

        logEvent("Processing", event);

        switch (event.getEventType()) {
            case ARRIVAL:
                nextEvents.addAll(generateServiceEventFromArrival(event));
                center.arrival(job);
                nextEvents.addAll(generateArrivalEventFromArrival(event));

                break;

            case START_PROCESS:
                List<ServingGroup<RiderGroup>> startedJobs = center.startService();
                nextEvents.addAll(generateNextEventsFromStart(event, startedJobs));
                break;

            case END_PROCESS:
                RiderGroup endedJob = event.getJob();
                center.endService(endedJob);
                nextEvents.addAll(generateNextEventsFromEnd(event));
                break;
        }

        return nextEvents;
    }

    private List<Event<RiderGroup>> generateServiceEventFromArrival(Event<RiderGroup> event) {
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        Center<RiderGroup> center = event.getEventCenter();
        double currentTime = ClockHandler.getInstance().getClock();

        if (center.isCenterEmpty()) {
            // Starting service immediately
            Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.START_PROCESS,
                    event.getJob(), currentTime);
            newEventList.add(newEvent);
        }

        for (Event<RiderGroup> newEvent : newEventList) {
            logEvent("Generated", newEvent);
        }

        return newEventList;
    }

    private List<Event<RiderGroup>> generateArrivalEventFromArrival(Event<RiderGroup> event) {
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        Center<RiderGroup> center = event.getEventCenter();

        if (center instanceof Entrance) {
            // Has to schedule new arrival event to the system
            Event<RiderGroup> newArrivalEvent = generateArrivalEvent();
            newEventList.add(newArrivalEvent);
        }

        for (Event<RiderGroup> newEvent : newEventList) {
            logEvent("Generated", newEvent);
        }

        return newEventList;
    }

    private List<Event<RiderGroup>> generateNextEventsFromStart(Event<RiderGroup> event,
            List<ServingGroup<RiderGroup>> startedJobs) {
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        Center<RiderGroup> center = event.getEventCenter();
        double currentTime = ClockHandler.getInstance().getClock();

        // TODO Add different managing for different kinds of centers
        // In this way we are generating multiple events with same end instant for all
        // jobs on the same attraction ride
        for (ServingGroup<RiderGroup> servingGroup : startedJobs) {
            Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.END_PROCESS,
                    servingGroup.getGroup(),
                    currentTime + servingGroup.getServiceTime());
            newEventList.add(newEvent);

            logEvent("Generated", newEvent);
        }

        return newEventList;
    }

    private List<Event<RiderGroup>> generateNextEventsFromEnd(Event<RiderGroup> event) {
        // TODO To schedule next service event in attraction we have to check if the
        // system is not empty
        List<Event<RiderGroup>> newEventList = new ArrayList<>();
        double currentTime = ClockHandler.getInstance().getClock();
        RiderGroup completedJob = event.getJob();

        Center<RiderGroup> nextCenter = networkRoutingNode.route(completedJob);
        if (nextCenter == null) {
            // TODO Manage exit from system
            // Take stats and print on csv
            // Find a way to manage
            System.out.println("Job exits from System");
        } else {
            Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(nextCenter, EventType.ARRIVAL,
                    completedJob, currentTime);
            newEventList.add(newEvent);
            logEvent("Generated", newEvent);
        }

        return newEventList;
    }

    public Event<RiderGroup> generateArrivalEvent() {
        Center<RiderGroup> entranceCenter = centersManager.getCenterByName(Config.ENTRANCE);
        return EventBuilder.getNewArrivalEvent(entranceCenter);
    }

    public void setCenters(List<Center<RiderGroup>> centerList) {
        this.centersManager.addCenterList(centerList);
    }

    private void logEvent(String processingType, Event event) {
        System.out.println("Simulation Type >> " + processingType);
        System.out.println("Event Type >> " + event.getEventType().name());
        System.out.println("Center >>> " + event.getEventCenter().getName());
        System.out.println("Event Time >>> " + event.getEventTime());
        System.out.println("Simulation Clock >>> " + ClockHandler.getInstance().getClock());
        System.out.println("");
    }

}

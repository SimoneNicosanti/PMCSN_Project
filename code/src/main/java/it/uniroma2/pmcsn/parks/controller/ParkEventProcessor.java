package it.uniroma2.pmcsn.parks.controller;

public class ParkEventProcessor {

    // private CentersManager<RiderGroup> centersManager;
    // private NetworkRoutingNode networkRoutingNode;

    // public ParkEventProcessor() {
    //     this.centersManager = new CentersManager<>();

    //     // Just for testing, delete once the system is live
    //     List<CenterInterface<RiderGroup>> centers = TestingUtils.createTestingCentersList();

    //     this.centersManager.addCenterList(centers);

    //     // Create the Network Routing Node
    //     List<Attraction> attractions = this.centersManager.getAttractions();
    //     List<Restaurant> restaurants = this.centersManager.getRestaurants();
    //     this.networkRoutingNode = new NetworkRoutingNode(new AttractionRoutingNode(attractions),
    //             new RestaurantRoutingNode(restaurants));
    // }

    // @Override
    // public List<Event<RiderGroup>> processEvent(Event<RiderGroup> event) {
    //     CenterInterface<RiderGroup> center = event.getEventCenter();
    //     RiderGroup job = event.getJob();
    //     List<Event<RiderGroup>> nextEvents = new ArrayList<>();

    //     EventLogger.logEvent("Processing", event);

    //     switch (event.getEventType()) {
    //         case ARRIVAL:
    //             center.arrival(job);
    //             break;

    //         case END_PROCESS:
    //             RiderGroup endedJob = event.getJob();
    //             center.endService(endedJob);
    //             break;
    //     }

    //     return nextEvents;
    // }

    // /*
    //  * Arrival:
    //  * 1. call center.arrival()
    //  * - check if isQueueEmptyAndCanServe()
    //  * - enqueue
    //  * - return condition
    //  * 2. if returnedCondition
    //  * - startedList = center.startService()
    //  * - generate end event for started jobs
    //  * End Service:
    //  * 1. ...
    //  * 2. if canServe
    //  * 3. startedJobList = startProcess()
    //  * 4. generate end event for started jobs
    //  */

    // /*
    //  * - EventPool singleton shared among the centers
    //  * - Fake center for exiting jobs for collecting stats FATTO
    //  * - Each center has the next routingNode, so can retrieve the next center for
    //  * generating a new event using ".route()"
    //  *
    //  * Arrival:
    //  * 1. call center.arrival()
    //  * - if isQueueEmptyAndCanServe(), then startServe which will generate a next
    //  * EndProcess event
    //  * - else enqueue
    //  * - if Entrance regenerate arrival event
    //  * End Service:
    //  * 1. ...
    //  * 2. center.endService()
    //  * - free served jobs
    //  * - try starting the next job, if Attraction wait all jobs and then start a new
    //  * ride
    //  * 
    //  */

    // private List<Event<RiderGroup>> generateServiceEventFromArrival(Event<RiderGroup> event) {
    //     List<Event<RiderGroup>> newEventList = new ArrayList<>();
    //     CenterInterface<RiderGroup> center = event.getEventCenter();
    //     double currentTime = ClockHandler.getInstance().getClock();

    //     // If the job of the arrival event can be served by the center, we generate a
    //     // START_PROCESS event
    //     if (center.isQueueEmptyAndCanServe(event.getJob().getGroupSize())) {
    //         // Starting service immediately
    //         Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.START_PROCESS,
    //                 event.getJob(), currentTime);
    //         newEventList.add(newEvent);
    //     }

    //     for (Event<RiderGroup> newEvent : newEventList) {
    //         EventLogger.logEvent("Generated", newEvent);
    //     }

    //     return newEventList;
    // }

    // private List<Event<RiderGroup>> generateArrivalEventFromArrival(Event<RiderGroup> event) {
    //     List<Event<RiderGroup>> newEventList = new ArrayList<>();
    //     CenterInterface<RiderGroup> center = event.getEventCenter();

    //     if (center instanceof Entrance) {
    //         // Has to schedule new arrival event to the system
    //         Event<RiderGroup> newArrivalEvent = generateArrivalEvent();
    //         newEventList.add(newArrivalEvent);
    //     }

    //     for (Event<RiderGroup> newEvent : newEventList) {
    //         EventLogger.logEvent("Generated", newEvent);
    //     }

    //     return newEventList;
    // }

    // private List<Event<RiderGroup>> generateNextEventsFromStart(Event<RiderGroup> event,
    //         List<ServingGroup<RiderGroup>> startedJobs) {
    //     List<Event<RiderGroup>> newEventList = new ArrayList<>();
    //     CenterInterface<RiderGroup> center = event.getEventCenter();
    //     double currentTime = ClockHandler.getInstance().getClock();

    //     // TODO Add different managing for different kinds of centers
    //     // In this way we are generating multiple events with same end instant for all
    //     // jobs on the same attraction ride
    //     for (ServingGroup<RiderGroup> servingGroup : startedJobs) {
    //         Event<RiderGroup> newEvent = EventBuilder.buildEventFrom(center, EventType.END_PROCESS,
    //                 servingGroup.getGroup(),
    //                 currentTime + servingGroup.getNewServiceTime());
    //         newEventList.add(newEvent);

    //         EventLogger.logEvent("Generated", newEvent);
    //     }

    //     return newEventList;
    // }

    // private List<Event<RiderGroup>> generateNextEventsFromEnd(Event<RiderGroup> event) {
    //     List<Event<RiderGroup>> newEventList = new ArrayList<>();
    //     double currentTime = ClockHandler.getInstance().getClock();
    //     RiderGroup completedJob = event.getJob();

    //     CenterInterface<RiderGroup> nextCenter = networkRoutingNode.route(completedJob);
    //     if (nextCenter == null) {
    //         // TODO Manage exit from system
    //         // Take stats and print on csv
    //         // Find a way to manage
    //         EventLogger.logExit(ClockHandler.getInstance().getClock());
    //         System.out.println("Job exits from System");
    //     } else {
    //         // Scheduling arrival to next center
    //         newEventList.add(EventBuilder.buildEventFrom(nextCenter, EventType.ARRIVAL, completedJob, currentTime));
    //     }

    //     // Scheduling new service on the center that has just finished serving: we have
    //     // to
    //     // generate the new start only if the center is not empty, otherwise we would
    //     // generate a not used service time (or we have to change the interface and if
    //     // the newServingJobs list is empty we can return a 0 service time)
    //     // TODO Check if it is better to move
    //     if (event.getEventCenter().canServe(event.getJob().getGroupSize())) {
    //         newEventList.add(
    //                 EventBuilder.buildEventFrom(event.getEventCenter(), EventType.START_PROCESS, completedJob,
    //                         currentTime));
    //     }

    //     for (Event<RiderGroup> newEvent : newEventList) {
    //         EventLogger.logEvent("Generated", newEvent);
    //     }

    //     return newEventList;
    // }

    // public Event<RiderGroup> generateArrivalEvent() {
    //     CenterInterface<RiderGroup> entranceCenter = centersManager.getCenterByName(Config.ENTRANCE);
    //     return EventBuilder.getNewArrivalEvent(entranceCenter);
    // }

    // public void setCenters(List<CenterInterface<RiderGroup>> centerList) {
    //     this.centersManager.addCenterList(centerList);
    // }

}

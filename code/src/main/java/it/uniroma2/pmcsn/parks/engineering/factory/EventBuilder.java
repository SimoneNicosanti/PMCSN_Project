package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.RandomStreams;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.Center;

public class EventBuilder extends RandomStreams {
    private final EventType eventType;
    private final Center<RiderGroup> center;

    public EventBuilder(EventType eventType, Center<RiderGroup> center) {
        this.eventType = eventType;
        this.center = center;
    }

    public static <T> Event<T> buildEventFrom(Event<T> event, EventType newEventType) {
        if (event.getEventType() == newEventType) {
            throw new RuntimeException("Cannot produce a " + newEventType + " event from itself");
        }
        Center<T> center = event.getEventCenter();
        EventsPoolId poolId = new EventsPoolId(center.getName(), newEventType);
        return new Event<>(poolId, center, ClockHandler.getInstance().getClock(), event.getJob());
    }

    // ** Builds an arrival associated to the Entrance Center */
    public Event<RiderGroup> buildEntranceArrivalEvent() {
        if (eventType != EventType.ARRIVAL || center.getName() != Config.ENTRANCE) {
            throw new RuntimeException("The builder does not match the requested event");
        }
        // Build the event and add the stream associated to it
        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        double currentTime = ClockHandler.getInstance().getClock();
        RiderGroup job = buildRiderGroup(currentTime);
        Event<RiderGroup> event = new Event<>(poolId, center, currentTime, job);
        this.addStream(event.getName());
        // Get the event stream index
        int streamIndex = this.getStream(event.getName());
        // TODO find a correct distribution for coming jobs
        // Add the distribution time to the event, based on the arrival time
        double arrivalTime = RandomHandler.getInstance().getExponential(streamIndex, 1);
        event.addDistributionTime(arrivalTime);

        return event;
    }

    private RiderGroup buildRiderGroup(double currentTime) {
        int groupSize = 0; // TODO find a distribution for group sizes
        GroupPriority priority = GroupPriority.NORMAL; // TODO find a distribution for group priorities

        return new RiderGroup(groupSize, priority, currentTime);
    }

}

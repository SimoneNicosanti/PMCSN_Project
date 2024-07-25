package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.Config;
import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class EventBuilder {

    private static int riderGroupId = 0;

    public static Event<RiderGroup> getNewArrivalEvent(CenterInterface<RiderGroup> arrivalCenter) {
        // TODO Manage distributions
        double interarrivalTime = RandomHandler.getInstance().getExponential("ARRIVAL BUILDER - ARRIVAL DISTRIBUTION",
                1);

        int groupSize = Double.valueOf(RandomHandler.getInstance().getUniform("ARRIVAL BUILDER - GROUP SIZE", 1, 10))
                .intValue();
        GroupPriority priority = computeGroupPriority();
        RiderGroup riderGroup = new RiderGroup(riderGroupId, groupSize, priority,
                ClockHandler.getInstance().getClock() + interarrivalTime);

        Event<RiderGroup> arrivalEvent = buildEventFrom(arrivalCenter, EventType.ARRIVAL,
                riderGroup, ClockHandler.getInstance().getClock() + interarrivalTime);

        riderGroupId++;

        return arrivalEvent;

    }

    private static GroupPriority computeGroupPriority() {
        double groupPriorityProb = RandomHandler.getInstance().getRandom("ARRIVAL BUILDER - PRIORITY");
        if (groupPriorityProb < Config.PRIORITY_PASS_PROB) {
            return GroupPriority.PRIORITY;
        } else {
            return GroupPriority.NORMAL;
        }
    }

    // Builds a new generic event
    public static Event<RiderGroup> buildEventFrom(CenterInterface<RiderGroup> center, EventType eventType,
            RiderGroup job,
            double eventTime) {
        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        return new Event<>(poolId, center, eventTime, job);
    }

}

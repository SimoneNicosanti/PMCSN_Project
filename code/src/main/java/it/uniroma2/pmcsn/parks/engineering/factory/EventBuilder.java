package it.uniroma2.pmcsn.parks.engineering.factory;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.ConfigHandler;
import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;
import it.uniroma2.pmcsn.parks.model.event.Event;
import it.uniroma2.pmcsn.parks.model.event.EventType;
import it.uniroma2.pmcsn.parks.model.event.EventsPoolId;
import it.uniroma2.pmcsn.parks.model.job.GroupPriority;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class EventBuilder {

    private static Long riderGroupId = 0L;

    public static Event<RiderGroup> getNewArrivalEvent(Center<RiderGroup> arrivalCenter) {
        Double arrivalRate = ConfigHandler.getInstance().getCurrentArrivalRate();
        // If arrivalRate == 0, stop the arrival
        if (arrivalRate == 0.0) {
            return null;
        }
        // TODO Manage distributions
        double interarrivalTime = RandomHandler.getInstance().getExponential(Constants.ARRIVAL_STREAM,
                arrivalRate);

        int groupSize = Double.valueOf(RandomHandler.getInstance().getUniform(Constants.GROUP_SIZE_STREAM, 1, 10))
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
        double groupPriorityProb = RandomHandler.getInstance().getRandom(Constants.PRIORITY_STREAM);
        if (groupPriorityProb < Constants.PRIORITY_PASS_PROB) {
            return GroupPriority.PRIORITY;
        } else {
            return GroupPriority.NORMAL;
        }
    }

    // Builds a new generic event
    public static Event<RiderGroup> buildEventFrom(Center<RiderGroup> center, EventType eventType,
            RiderGroup job,
            double eventTime) {
        EventsPoolId poolId = new EventsPoolId(center.getName(), eventType);
        return new Event<>(poolId, center, eventTime, job);
    }

}

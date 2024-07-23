package it.uniroma2.pmcsn.parks.model.job;

import it.uniroma2.pmcsn.parks.model.stats.GroupStats;

public class RiderGroup {

    private int groupSize;
    private GroupPriority priority;
    private GroupStats stats;

    public RiderGroup(int groupSize, GroupPriority groupPriority, double systemEntranceTime) {
        this.groupSize = groupSize;
        this.priority = groupPriority;
        this.stats = new GroupStats(systemEntranceTime);
    }

    public int getGroupSize() {
        return this.groupSize;
    }

    public GroupPriority getPriority() {
        return priority;
    }

    public GroupStats getGroupStats() {
        return this.stats;
    }

}

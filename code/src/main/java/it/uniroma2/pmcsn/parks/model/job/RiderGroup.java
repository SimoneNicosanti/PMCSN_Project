package it.uniroma2.pmcsn.parks.model.job;

import it.uniroma2.pmcsn.parks.model.stats.GroupStats;

public class RiderGroup {

    private int groupId;
    private int groupSize;
    private GroupPriority priority;
    private GroupStats stats;

    public RiderGroup(int groupId, int groupSize, GroupPriority groupPriority, double systemEntranceTime) {
        this.groupId = groupId;
        this.groupSize = groupSize;
        this.priority = groupPriority;
        this.stats = new GroupStats(systemEntranceTime);
    }

    public int getGroupId() {
        return this.groupId;
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

package it.uniroma2.pmcsn.parks.model.job;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.stats.GroupStats;

public class RiderGroup {

    private Long groupId;
    private int groupSize;
    private GroupPriority priority;
    private GroupStats stats;

    public RiderGroup(Long groupId, int groupSize, GroupPriority groupPriority, double systemEntranceTime) {
        this.groupId = groupId;
        this.groupSize = groupSize;
        this.priority = groupPriority;
        this.stats = new GroupStats(systemEntranceTime);
    }

    public Long getGroupId() {
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

    public boolean isSmallGroup() {
        return (this.priority == GroupPriority.NORMAL && this.groupSize <= Constants.SMALL_GROUP_LIMIT_SIZE);
    }

}

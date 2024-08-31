package it.uniroma2.pmcsn.parks.model.stats;

import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.model.queue.QueuePriority;

public class CenterStatistics {

    private AreaStats serviceAreaGroup;
    private AreaStats serviceAreaPeople;

    private AreaStats queueAreaGroup;
    private AreaStats queueAreaPeople;
    private Map<QueuePriority, AreaStats> queueAreaPerPrioGroup;
    private Map<QueuePriority, AreaStats> queueAreaPerPrioPeople;

    private Integer sampleIdx;

    public CenterStatistics() {

        // NEW STATS
        this.serviceAreaGroup = new AreaStats();
        this.serviceAreaPeople = new AreaStats();

        this.queueAreaGroup = new AreaStats();
        this.queueAreaPeople = new AreaStats();

        this.queueAreaPerPrioGroup = new HashMap<>();
        this.queueAreaPerPrioPeople = new HashMap<>();

        this.sampleIdx = 0;
    }

    // Multiplier is intended as the number by which the area has to be multiplied
    // In case of per person statistic and a k-erlang distribution, the time is the
    // total so we have to multiply by 1 and not by the size of the group
    public void updateServiceArea(Double time, Integer groupSize, Integer multiplier) {
        this.serviceAreaGroup.updateArea(time, 1, 1);
        this.serviceAreaPeople.updateArea(time, groupSize, multiplier);
    }

    public void updateQueueArea(Double time, QueuePriority prio, Integer groupSize) {
        if (this.queueAreaPerPrioGroup.get(prio) == null) {
            this.queueAreaPerPrioGroup.put(prio, new AreaStats());
            this.queueAreaPerPrioPeople.put(prio, new AreaStats());
        }
        this.queueAreaPerPrioGroup.get(prio).updateArea(time, 1, 1);
        this.queueAreaPerPrioPeople.get(prio).updateArea(time, groupSize, groupSize);

        this.queueAreaGroup.updateArea(time, 1, 1);
        this.queueAreaPeople.updateArea(time, groupSize, groupSize);
    }

    public Double getResponseAreaValue(StatsType statsType) {
        AreaStats serviceArea;
        AreaStats queueArea;
        switch (statsType) {
            case GROUP:
                serviceArea = this.serviceAreaGroup;
                queueArea = this.queueAreaGroup;
                break;

            case PERSON:
                serviceArea = this.serviceAreaPeople;
                queueArea = this.queueAreaPeople;
                break;

            default:
                return null;
        }

        return serviceArea.getArea() + queueArea.getArea();
    }

    public AreaStats getServiceAreaStats(StatsType statsType) {
        return switch (statsType) {
            case GROUP -> this.serviceAreaGroup;
            case PERSON -> this.serviceAreaPeople;
        };
    }

    public AreaStats getQueueAreaStats(StatsType statsType, QueuePriority queuePrio) {

        if (queuePrio == null) {
            return switch (statsType) {
                case GROUP -> this.queueAreaGroup;
                case PERSON -> this.queueAreaPeople;
            };
        }

        return switch (statsType) {
            case GROUP -> this.queueAreaPerPrioGroup.get(queuePrio);
            case PERSON -> this.queueAreaPerPrioPeople.get(queuePrio);
        };

    }

    public Map<QueuePriority, AreaStats> getAllQueueAreaStats(StatsType statsType) {
        return switch (statsType) {
            case GROUP -> this.queueAreaPerPrioGroup;
            case PERSON -> this.queueAreaPerPrioPeople;
        };
    }

    public void sampleStats() {
        for (QueuePriority prio : queueAreaPerPrioPeople.keySet()) {
            this.queueAreaPerPrioPeople.get(prio).sampleAverage(sampleIdx);
        }
        sampleIdx += 1;
    }
}

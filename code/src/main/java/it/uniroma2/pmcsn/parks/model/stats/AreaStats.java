package it.uniroma2.pmcsn.parks.model.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public class AreaStats {

    private Double area;
    private Integer size;
    private Double lastUpdateClock;

    private Map<Integer, Double> areaSamples;

    public AreaStats() {
        this.area = 0.0;
        this.size = 0;
        this.lastUpdateClock = 0.0;

        this.areaSamples = new HashMap<>();
    }

    public void sampleAverage(int sampleIdx) {
        if (this.size == 0) {
            this.areaSamples.put(sampleIdx, 0.0);
        } else {
            this.areaSamples.put(sampleIdx, getSizeAvgdStat());
        }
    }

    public Map<Integer, Double> getSampleList() {
        return this.areaSamples;
    }

    public void updateArea(Double areaInc, Integer sizeInc, Integer multiplier) {
        this.area += areaInc * multiplier;
        this.size += sizeInc;
        this.lastUpdateClock = ClockHandler.getInstance().getClock();
    }

    public Double getSizeAvgdStat() {
        return this.area / this.size;
    }

    public Double getTimeAvgdStat() {
        return this.area / this.lastUpdateClock;
    }

    public Integer getSize() {
        return this.size;
    }

    public Double getArea() {
        return this.area;
    }
}

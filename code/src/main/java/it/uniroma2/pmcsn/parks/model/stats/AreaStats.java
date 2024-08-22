package it.uniroma2.pmcsn.parks.model.stats;

import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public class AreaStats {

    private Double area;
    private Integer size;
    private Double lastUpdateClock;

    public AreaStats() {
        this.area = 0.0;
        this.size = 0;
        this.lastUpdateClock = 0.0;
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

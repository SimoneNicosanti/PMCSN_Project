package it.uniroma2.pmcsn.parks.model.stats;

public class AreaStats {

    private Double area;
    private Integer size;

    public AreaStats() {
        this.area = 0.0;
        this.size = 0;
    }

    public void updateArea(Double areaInc, Integer sizeInc, Integer multiplier) {
        this.area += areaInc * multiplier;
        this.size += sizeInc;
    }

    public Double getSizeAvgdStat() {
        return this.area / this.size;
    }

    public Integer getSize() {
        return this.size;
    }

    public Double getArea() {
        return this.area;
    }
}

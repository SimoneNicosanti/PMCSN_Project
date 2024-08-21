package it.uniroma2.pmcsn.parks.model;

public class Interval {
    private Double start;
    private Double end;
    private Integer index;

    public Integer getIndex() {
        return index;
    }

    public Interval(Double start, Double end, Integer index) {
        this.start = start;
        this.end = end;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        Interval interval = (Interval) o;
        return interval.end == this.end && interval.start == this.start;
    }

    @Override
    public String toString() {
        return this.index + " [" + this.start + "-" + this.end + "]";
    }

    public boolean contains(Double time) {
        // start <= time < end
        return this.start <= time && time < this.end;
    }

    public double getStart() {
        return this.start;
    }

    public double getEnd() {
        return this.end;
    }

    public double getSize() {
        return this.end - this.start;
    }

}
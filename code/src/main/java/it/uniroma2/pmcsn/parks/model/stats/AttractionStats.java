package it.uniroma2.pmcsn.parks.model.stats;

// Implement this for attraction custom attributes
public class AttractionStats extends CenterStats {
    public AttractionStats() {

    }

    @Override
    public double getAvgServiceTime() {
        return this.serviceTime / 1;
    }

}
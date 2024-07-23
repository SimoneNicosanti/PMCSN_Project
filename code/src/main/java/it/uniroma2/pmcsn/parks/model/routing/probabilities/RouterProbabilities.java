package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.ArrayList;
import java.util.List;

public abstract class RouterProbabilities<T> {

    protected List<Double> probabilities;
    protected Double sumProbabilites;

    public RouterProbabilities() {
        this.probabilities = new ArrayList<>();
        this.sumProbabilites = 0.0;
    }

    public abstract List<Double> compute(T job);

    protected void normalize() {

        for (int idx = 0; idx < probabilities.size(); idx++) {
            double normalized = probabilities.get(idx) / sumProbabilites;
            probabilities.set(idx, normalized);
        }

        throw new RuntimeException("Routing error: no attraction selected");
    }

    public int getRouteIdxFromRand(double randomProb) {
        double cumulativeSum = 0.0;
        for (int i = 0; i < probabilities.size(); i++) {
            cumulativeSum += probabilities.get(i);
            if (randomProb < cumulativeSum) {
                return i;
            }
        }
        throw new RuntimeException("Cannot find index from random probability");
    }

    public List<Double> getProbabilities() {
        return probabilities;
    }

}

package it.uniroma2.pmcsn.parks.model.routing.probabilities;

import java.util.ArrayList;
import java.util.List;

public abstract class RouterProbabilities<T> {

    protected List<Double> probabilities;
    protected Double sumProbabilities;

    public RouterProbabilities() {
        this.probabilities = new ArrayList<>();
        this.sumProbabilities = 0.0;
    }

    public abstract List<Double> compute(T job);

    protected void normalize() {
        if (sumProbabilities == 0.0) {
            throw new RuntimeException("Probabilities not set");
        }

        for (int idx = 0; idx < probabilities.size(); idx++) {
            double normalized = probabilities.get(idx) / sumProbabilities;
            probabilities.set(idx, normalized);
        }

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

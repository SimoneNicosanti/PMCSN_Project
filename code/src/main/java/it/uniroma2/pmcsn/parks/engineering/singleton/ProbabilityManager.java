package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class ProbabilityManager {

    private static ProbabilityManager instance = null ;

    private Map<String, Double> probabilityMap;

    private ProbabilityManager() {
        this.probabilityMap = new HashMap<>();
    }

    // NB: The Probability Manager must be filled after the first creation
    public static ProbabilityManager getInstance() {
        if (instance == null) {
            instance = new ProbabilityManager() ;
        }
        return instance ;
    }

    public double getProbability(String nodeName) {
        return probabilityMap.get(nodeName);
    }

    // Change the probabilities of the input nodes 
    public void changeProbabilities(List<Pair<String, Double>> probabilities) {

        for (Pair<String, Double> pair : probabilities) {
            probabilityMap.put(pair.getLeft(), pair.getRight());
        }
    }


}

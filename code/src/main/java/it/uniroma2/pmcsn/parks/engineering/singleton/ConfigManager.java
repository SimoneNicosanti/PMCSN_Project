package it.uniroma2.pmcsn.parks.engineering.singleton;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import it.uniroma2.pmcsn.parks.model.Interval;

public class ConfigManager {

    private static ConfigManager instance = null;

    // Key: (RotuingNodeName, Interval), Value: probability
    private Map<Pair<String, Interval>, Double> intervalProbabilities;

    private ConfigManager() {
        this.intervalProbabilities = new HashMap<>();
    }

    // NB: The Probability Manager must be filled after the first creation
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public double getProbability(String nodeName, Interval interval) {
        Pair<String, Interval> key = Pair.of(nodeName, interval);
        return intervalProbabilities.get(key);
    }

    // Change the probability of the key (routingNodeName, interval)
    public void changeProbability(String centerName, Interval interval, double probability) {
        intervalProbabilities.put(Pair.of(centerName, interval), probability);
    }

}

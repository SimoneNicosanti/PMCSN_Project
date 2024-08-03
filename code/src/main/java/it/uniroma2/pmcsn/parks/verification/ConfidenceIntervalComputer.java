package it.uniroma2.pmcsn.parks.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Center;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;
import it.uniroma2.pmcsn.parks.model.server.concrete_servers.StatsCenter;
import it.uniroma2.pmcsn.parks.model.stats.BatchStats;
import it.uniroma2.pmcsn.parks.model.stats.CenterStatistics;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;
import it.uniroma2.pmcsn.parks.random.Estimate;

public class ConfidenceIntervalComputer {

    private class StatsValues {
        private Map<String, List<Double>> statsValues;

        public StatsValues() {
            this.statsValues = new HashMap<>();
        }

        public void addStatsValue(String statsName, Double statsValue) {
            if (statsValues.get(statsName) == null) {
                statsValues.put(statsName, new ArrayList<>());
            }
            statsValues.get(statsName).add(statsValue);
        }

        public void addAllValues(String statsName, List<Double> statsValue) {
            if (statsValues.get(statsName) == null) {
                statsValues.put(statsName, new ArrayList<>());
            }

            statsValues.get(statsName).addAll(statsValue);
        }

        public Map<String, List<Double>> getValues() {
            return statsValues;
        }
    }

    public record ConfidenceInterval(
            String centerName,
            String statsName,
            Double mean,
            Double interval) {
    }

    private Map<String, StatsValues> valuesMap;

    public ConfidenceIntervalComputer() {
        this.valuesMap = new HashMap<>();
    }

    public void updateStatistics(List<Center<RiderGroup>> centerList) {
        for (Center<RiderGroup> center : centerList) {
            if (valuesMap.get(center.getName()) == null) {
                valuesMap.put(center.getName(), new StatsValues());
            }

            BatchStats serviceBatchStats = ((StatsCenter) center).getServiceBatchStats();
            BatchStats queueBatchStats = ((StatsCenter) center).getQueueBatchStats();

            valuesMap.get(center.getName()).addAllValues("ServiceTime", serviceBatchStats.getAverages());
            valuesMap.get(center.getName()).addAllValues("QueueTime", queueBatchStats.getAverages());

            // valuesMap.get(center.getName()).addStatsValue(
            // "ServiceTime", centerStatistics.getAvgServiceTimePerGroup());
            // valuesMap.get(center.getName()).addStatsValue(
            // "QueueTime",
            // centerStatistics.getAvgGroupQueueTimeByArea());
        }
    }

    public List<ConfidenceInterval> computeConfidenceIntervals() {
        List<ConfidenceInterval> returnList = new ArrayList<>();
        for (String centerName : valuesMap.keySet()) {
            StatsValues values = valuesMap.get(centerName);
            for (String statsName : values.getValues().keySet()) {
                List<Double> valuesList = values.getValues().get(statsName);
                Double mean = computeMean(valuesList);
                Double interval = Estimate.computeConfidenceInterval(valuesList, 0.95);
                returnList.add(new ConfidenceInterval(centerName, statsName, mean, interval));
            }
        }
        return returnList;
    }

    private Double computeMean(List<Double> valuesList) {
        if (valuesList.size() == 0) {
            return 0.0;
        }
        Double sum = 0.0;
        for (Double elem : valuesList) {
            sum += elem;
        }
        return sum / valuesList.size();
    }

}

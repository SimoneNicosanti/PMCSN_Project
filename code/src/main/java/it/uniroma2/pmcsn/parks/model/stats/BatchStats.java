package it.uniroma2.pmcsn.parks.model.stats;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;

public class BatchStats {

    private List<Double> timeList;

    public BatchStats() {
        this.timeList = new ArrayList<>();
    }

    public void addTime(Double newTime) {
        if (timeList.size() + 1 > Constants.BATCH_SIZE * Constants.BATCH_NUMBER) {
            return;
        }
        timeList.add(newTime);
    }

    public boolean isBatchCompleted() {
        return (timeList.size() == Constants.BATCH_SIZE * Constants.BATCH_NUMBER);
    }

    public List<Double> getAverages() {
        List<Double> averages = new ArrayList<>();

        int batchSize = Constants.BATCH_SIZE;

        for (int i = 0; i < Constants.BATCH_NUMBER; i++) {
            double sum = 0.0;
            for (int j = 0; j < batchSize; j++) {
                sum += timeList.get(i * batchSize + j);
            }
            averages.add(sum / batchSize);
        }

        return averages;
    }
}

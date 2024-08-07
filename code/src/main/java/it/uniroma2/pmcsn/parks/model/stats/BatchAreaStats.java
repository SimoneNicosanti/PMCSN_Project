package it.uniroma2.pmcsn.parks.model.stats;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public class BatchAreaStats {

    List<Double> areaValues;
    List<Double> batchDurationList;
    Double prevClosing;

    public BatchAreaStats() {
        this.areaValues = new ArrayList<>();
        this.batchDurationList = new ArrayList<>();
        this.prevClosing = 0.0;
    }

    public void addArea(Double area) {
        // Check if every batch is already full
        if (areaValues.size() + 1 > Constants.BATCH_SIZE * Constants.BATCH_NUMBER) {
            return;
        }

        // Add the new value to the list
        this.areaValues.add(area);

        // If the current batch is full, save the duration of the batch
        if (areaValues.size() % Constants.BATCH_SIZE == 0) {
            Double currentTime = ClockHandler.getInstance().getClock();
            Double duration = currentTime - prevClosing;
            this.prevClosing = currentTime;

            batchDurationList.add(duration);
        }
    }

    public boolean isBatchClosed() {
        return areaValues.size() == Constants.BATCH_SIZE * Constants.BATCH_NUMBER;
    }

    public List<Double> getAverages() {
        List<Double> avgs = new ArrayList<>();

        int batchSize = Constants.BATCH_SIZE;

        for (int i = 0; i < Constants.BATCH_NUMBER; i++) {
            double sum = 0.0;
            for (int j = 0; j < batchSize; j++) {
                sum += areaValues.get(i * batchSize + j);
            }
            avgs.add(sum / batchSize);
        }

        return avgs;
    }
}

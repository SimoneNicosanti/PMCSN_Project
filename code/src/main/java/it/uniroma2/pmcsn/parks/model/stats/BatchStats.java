package it.uniroma2.pmcsn.parks.model.stats;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.Constants;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public class BatchStats {

    private List<Double> timeList;
    private List<Double> batchDurationList;
    private Double prevClosingTime;
    private String statName;

    private Integer jobsInBatch;
    private List<Double> areaList;
    private List<Double> serviceList;

    public BatchStats(String statName) {
        this.timeList = new ArrayList<>();
        this.batchDurationList = new ArrayList<>();
        this.statName = statName;
        this.prevClosingTime = 0.0;

        this.areaList = new ArrayList<>();
        this.serviceList = new ArrayList<>();
    }

    public void addArea(Double newArea) {
        if (this.jobsInBatch % Constants.BATCH_SIZE == 0
                && this.jobsInBatch < Constants.BATCH_SIZE * Constants.BATCH_NUMBER) {
            this.areaList.add(newArea);
        }
    }

    public void addTime(Double newTime) {
        if (timeList.size() + 1 > Constants.BATCH_SIZE * Constants.BATCH_NUMBER) {
            return;
        }
        timeList.add(newTime);

        if (timeList.size() % Constants.BATCH_SIZE == 0) {
            Double currentTime = ClockHandler.getInstance().getClock();
            Double duration = currentTime - prevClosingTime;
            this.batchDurationList.add(duration);

            this.prevClosingTime = currentTime;
        }
    }

    public boolean isBatchCompleted() {
        return (timeList.size() == Constants.BATCH_SIZE * Constants.BATCH_NUMBER);
    }

    public List<Double> getTimeAvgs() {
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

    public List<Double> getNumberAvgs() {
        List<Double> timeAvgs = this.getTimeAvgs();
        List<Double> avgs = new ArrayList<>();

        int batchSize = Constants.BATCH_SIZE;

        for (int i = 0; i < timeAvgs.size(); i++) {
            Double timeValue = timeAvgs.get(i);
            Double batchDuration = batchDurationList.get(i);

            double lambda = batchSize / batchDuration;
            // lambda * E[Tq] = E[Nq]
            avgs.add(lambda * timeValue);
        }

        return avgs;
    }

    public String getStatName() {
        return this.statName;
    }
}

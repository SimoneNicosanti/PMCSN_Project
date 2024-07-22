
package it.uniroma2.pmcsn.parks.model.server;

import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.RandomStreams;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;

public abstract class Center<T> extends RandomStreams {
    protected final String name;

    protected QueueManager<T> queueManager;

    protected List<T> currentServingJobs;
    protected double currentServiceTime;
    private final int slotNumber;

    public Center(String name, QueueManager<T> queueManager, int slotNumber) {
        this.name = name;
        this.queueManager = queueManager;
        this.currentServingJobs = null;
        this.currentServiceTime = 0.0;
        this.slotNumber = slotNumber;
        this.addStream(this.name);
        ;
    }

    public void arrival(T job) {
        double currentTime = ClockHandler.getInstance().getClock();
        queueManager.addToQueues(job, currentTime);

    }

    protected abstract double getDistribution();

    protected abstract List<T> terminateJobs();

    public double startService() {
        if (currentServingJobs != null) {
            throw new RuntimeException("Cannot start service, there are ongoing jobs to serve");
        }
        double currentTime = ClockHandler.getInstance().getClock();
        List<T> servingJobs = queueManager.extractFromQueues(slotNumber, currentTime);
        this.currentServingJobs = servingJobs;

        this.currentServiceTime = this.getDistribution();

        return this.currentServiceTime;
    }

    public List<T> endService() {
        if (currentServingJobs == null) {
            throw new RuntimeException("Cannot end service because there are no riders to serve");
        }
        List<T> terminatedJobs = this.terminateJobs();
        this.currentServingJobs = null;
        this.currentServiceTime = 0;

        return terminatedJobs;
    }

    public boolean isServing() {
        return currentServingJobs != null && queueManager.areQueuesEmpty();
    }

    public String getName() {
        return name;
    }
}
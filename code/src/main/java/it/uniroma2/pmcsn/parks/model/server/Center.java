package it.uniroma2.pmcsn.parks.model.server;

import it.uniroma2.pmcsn.parks.engineering.interfaces.CenterInterface;
import it.uniroma2.pmcsn.parks.engineering.interfaces.QueueManager;

public abstract class Center<T> implements CenterInterface<T> {
    protected final String name;

    protected QueueManager<T> queueManager;
    protected final Integer slotNumber;

    public Center(String name, QueueManager<T> queueManager, Integer slotNumber) {
        this.name = name;
        this.queueManager = queueManager;
        this.slotNumber = slotNumber;
    }

    @Override
    public boolean isQueueEmptyAndCanServe(Integer jobSize) {
        return this.queueManager.areQueuesEmpty() && canServe(jobSize);
    }

    @Override
    public void arrival(T job) {
        queueManager.addToQueues(job);
    }

    @Override
    public String getName() {
        return name;
    }

}
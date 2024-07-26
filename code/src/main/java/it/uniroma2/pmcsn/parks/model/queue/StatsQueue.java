package it.uniroma2.pmcsn.parks.model.queue;

import java.util.HashMap;
import java.util.Map;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.engineering.singleton.ClockHandler;
import it.uniroma2.pmcsn.parks.model.stats.QueueStats;

/**
 * StatsQueue implement the Decoretor GOF pattern. Its responsibility is to
 * collect data during the simulation. Implement the method retrieveStats to
 * collect stats.
 */
public abstract class StatsQueue<T> implements Queue<T> {

    private Queue<T> queue;
    private Map<T, Double> entranceTimeMap;
    protected QueueStats queueStats;

    protected StatsQueue(String priorityName, Queue<T> queue, QueuePriority prio) {
        this.queue = queue;
        this.entranceTimeMap = new HashMap<>();
        this.queueStats = new QueueStats(prio);
    }

    public QueueStats getQueueStats() {
        return this.queueStats;
    }

    protected abstract void retrieveStats(T item, double entranceTime, double waitingTime);

    @Override
    public void enqueue(T item) {
        entranceTimeMap.put(item, ClockHandler.getInstance().getClock());
        this.queue.enqueue(item);
    }

    @Override
    public T dequeue() {

        T item = this.queue.dequeue();

        if (item == null)
            return null;

        Double entranceTime = entranceTimeMap.get(item);

        // Integrity check: every enqued item should have been added in "enqueue" method
        if (entranceTime == null)
            throw new RuntimeException("The entrance time must be defined for the enqueued item");

        Double waitingTime = ClockHandler.getInstance().getClock() - entranceTime;

        this.retrieveStats(item, entranceTime, waitingTime);

        // Item is not in the map anymore
        entranceTimeMap.remove(item);

        return item;
    }

    @Override
    public int getNextSize() {
        return queue.getNextSize();
    }

    @Override
    public int queueLength() {
        return queue.queueLength();
    }

}
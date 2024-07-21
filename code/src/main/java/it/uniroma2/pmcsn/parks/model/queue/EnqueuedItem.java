package it.uniroma2.pmcsn.parks.model.queue;

public class EnqueuedItem<T> {

    private T enqueuedItem;
    private double queueEntranceTime;

    public EnqueuedItem(T enqueuedItem, double queueEntranceTime) {
        this.enqueuedItem = enqueuedItem;
        this.queueEntranceTime = queueEntranceTime;
    }

    public T getGroup() {
        return enqueuedItem;
    }

    public double getQueueEntranceTime() {
        return queueEntranceTime;
    }

}

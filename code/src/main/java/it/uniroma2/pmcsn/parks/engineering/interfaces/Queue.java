package it.uniroma2.pmcsn.parks.engineering.interfaces;

import it.uniroma2.pmcsn.parks.model.queue.EnqueuedItem;

public interface Queue<T> {

    public void enqueue(T item);

    public T dequeue();

    public int getNextSize();

    public int queueLength();

}

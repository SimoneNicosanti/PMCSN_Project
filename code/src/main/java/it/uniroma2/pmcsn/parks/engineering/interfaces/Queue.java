package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;

public interface Queue<T> {

    public void enqueue(T item);

    public T dequeue();

    public int getNextSize();

    public int queueLength();

    public List<T> dequeueAll();
}

package it.uniroma2.pmcsn.parks.engineering.interfaces;

import it.uniroma2.pmcsn.parks.model.queue.EnqueuedItem;

public interface Queue<T> {

    public void enqueue(EnqueuedItem<T> item) ;

    public T dequeue(double exitTime) ;

    public int getNextSize() ;

}

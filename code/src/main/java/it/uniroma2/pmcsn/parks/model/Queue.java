package it.uniroma2.pmcsn.parks.model;

import java.util.ArrayList;
import java.util.List;

public interface Queue {

    public void enqueue(RiderGroup group, double entranceTime) ;

    public RiderGroup dequeue(double exitTime) ;

    public int getNextSize() ;

}

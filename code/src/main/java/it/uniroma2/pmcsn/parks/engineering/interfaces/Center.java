

package it.uniroma2.pmcsn.parks.engineering.interfaces;

import java.util.List;


public interface Center<T> {

    public void arrival(T arriveItem, double currentTime) ;

    public List<T> endService() ;

    public double startService(double currentTime);

    public String getName();
}
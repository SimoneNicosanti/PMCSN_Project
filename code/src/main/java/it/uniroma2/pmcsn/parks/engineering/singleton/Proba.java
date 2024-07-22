package it.uniroma2.pmcsn.parks.engineering.singleton;

public class Proba {

    private double clock ;

    private static ClockHandler instance = null ;

    private ClockHandler() {
        this.clock = 0.0 ;
    }

    public static ClockHandler getInstance() {
        if (instance == null) {
            instance = new ClockHandler() ;
        }
        return instance ;
    }

    public double getClock() {
        return this.clock ;
    }

    public void setClock(double newClockValue) {
        this.clock = newClockValue ;
    }

}

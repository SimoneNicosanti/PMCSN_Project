package it.uniroma2.pmcsn.parks.model;


public class EnqueuedGroup {

    private RiderGroup group ;
    private double queueEntranceTime ;

    public EnqueuedGroup(RiderGroup group, double queueEntranceTime) {
        this.group = group ;
        this.queueEntranceTime = queueEntranceTime ;
    }

    public RiderGroup getGroup() {
        return group;
    }

    public double getQueueEntranceTime() {
        return queueEntranceTime;
    }

}

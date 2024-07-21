package it.uniroma2.pmcsn.parks.model.job;

public class ServingGroup {
    
    private RiderGroup group;
    private double initialTime;

    public ServingGroup(RiderGroup group, double initialTime) {
        this.group = group;
        this.initialTime = initialTime;
    }

    public RiderGroup getGroup() {
        return group;
    }

    public double getInitialTime() {
        return initialTime;
    }

}

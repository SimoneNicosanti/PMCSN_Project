package it.uniroma2.pmcsn.parks.model.job;

public class ServingGroup<T> {

    private T group;
    private double serviceTime;

    public ServingGroup(T group, double initialTime) {
        this.group = group;
        this.serviceTime = initialTime;
    }

    public T getGroup() {
        return group;
    }

    public double getServiceTime() {
        return serviceTime;
    }

}

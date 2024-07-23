package it.uniroma2.pmcsn.parks.model.queue;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.pmcsn.parks.engineering.interfaces.Queue;
import it.uniroma2.pmcsn.parks.model.job.RiderGroup;

public class FifoQueue implements Queue<RiderGroup> {

    private List<RiderGroup> queueList;

    public FifoQueue() {
        this.queueList = new ArrayList<>();
    }

    @Override
    public void enqueue(RiderGroup group) {
        queueList.add(group);
    }

    @Override
    public RiderGroup dequeue() {
        if (queueList.size() > 0) {
            return queueList.remove(0);
        }
        return null;
    }

    @Override
    public int getNextSize() {
        if (queueList.size() > 0) {
            return queueList.get(0).getGroupSize();
        }
        return 0;
    }

    @Override
    public int queueLength() {
        int sum = 0;
        for (RiderGroup group : this.queueList) {
            sum += group.getGroupSize();
        }
        return sum;
    }

}
package it.uniroma2.pmcsn.parks.engineering;

import it.uniroma2.pmcsn.parks.engineering.singleton.RandomHandler;

public class RandomStream {
    protected int streamIndex;

    public RandomStream() {
        this.streamIndex = RandomHandler.getInstance().getNewStreamIndex();
    }

}

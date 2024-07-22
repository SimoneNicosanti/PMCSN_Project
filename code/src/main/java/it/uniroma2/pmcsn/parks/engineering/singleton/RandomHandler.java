package it.uniroma2.pmcsn.parks.engineering.singleton;

import it.uniroma2.pmcsn.parks.random.Rngs ;
import it.uniroma2.pmcsn.parks.random.Rvgs ;

public class RandomHandler {

    private static long SEED = 123456;
    private static long MAX_STREAM_NUM = 256 ;

    private static RandomHandler instance = null ;

    private Rngs streamGenerator;
    private Rvgs distributionGenerator;
    private int streamCount;


    private RandomHandler() {
        this.streamGenerator = new Rngs() ;
        this.streamGenerator.plantSeeds(SEED) ; 
        this.distributionGenerator = new Rvgs(streamGenerator) ;

        this.streamCount = 0 ;
    }

    public static RandomHandler getInstance() {
        if (instance == null) {
            instance = new RandomHandler() ;
        }
        return instance ;
    }

    public int getNewStreamIndex() {
        int returnCounter = streamCount ;
        this.streamCount++ ;
        if (this.streamCount >= MAX_STREAM_NUM) {
            throw new RuntimeException("ERROR >>> Stream number excedeed") ;
        }
        return returnCounter ;
    }

    public double getRandom(int stream) {
        streamGenerator.selectStream(stream);
        return streamGenerator.random() ;
    }

    public double getUniform(int stream, double a, double b) {
        streamGenerator.selectStream(stream);
        return distributionGenerator.uniform(a, b) ;
    }

    public double getExponential(int stream, double m) {
        streamGenerator.selectStream(stream);
        return distributionGenerator.exponential(m);
    }

}

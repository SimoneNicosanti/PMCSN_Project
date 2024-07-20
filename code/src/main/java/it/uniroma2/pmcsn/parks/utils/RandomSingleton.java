package it.uniroma2.pmcsn.parks.utils;

import javax.management.RuntimeErrorException;

import it.uniroma2.pmcsn.parks.random.Rngs ;
import it.uniroma2.pmcsn.parks.random.Rvgs ;

public class RandomSingleton {

    private static long SEED = 123456;
    private static long MAX_STREAM_NUM = 256 ;

    private static RandomSingleton instance = null ;

    private Rngs streamGenerator;
    private Rvgs distributionGenerator;
    private int streamCount;


    private RandomSingleton() {
        this.streamGenerator = new Rngs() ;
        this.streamGenerator.plantSeeds(SEED) ; 
        this.distributionGenerator = new Rvgs(streamGenerator) ;

        this.streamCount = 0 ;
    }

    public static RandomSingleton getInstance() {
        if (instance == null) {
            instance = new RandomSingleton() ;
        }
        return instance ;
    }

    public int getNewStreamIndex() {
        int returnCounter = streamCount ;
        this.streamCount++ ;
        if (this.streamCount >= MAX_STREAM_NUM) {
            throw new RuntimeErrorException(null, "ERROR >>> Stream number excedeed") ;
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

}

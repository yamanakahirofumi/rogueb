package net.hero.rogueb.math;

import java.util.Random;

public class ReproducibleRandom {
    private final java.util.Random random;
    private final int seed;

    public ReproducibleRandom(int seed){
        this.seed = seed;
        this.random = new Random(seed);
    }

    public int getSeed() {
        return seed;
    }

    public int next(){
        return this.random.nextInt();
    }
}

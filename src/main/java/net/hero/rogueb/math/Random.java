package net.hero.rogueb.math;

public class Random {
    private static final java.util.Random random = new java.util.Random();

    public static int rnd(int range){
        if(range == 0){
            return 0;
        }
        return random.nextInt(range);
    }
}

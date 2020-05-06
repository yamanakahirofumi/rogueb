package net.hero.rogueb.math;

public class Random {
    private static final java.util.Random random = new java.util.Random();

    public static int rnd(int range) {
        if (range == 0) {
            return 0;
        }
        return random.nextInt(range);
    }

    /**
     * 正規分布
     *
     * @param ave 平均
     * @param dev 標準偏差
     * @return 平均ave標準偏差devの正規分布の擬似乱数値を四捨五入したもの
     */
    public static long gaussian(double ave, double dev) {
        double v = random.nextGaussian() * dev;
        return Math.round(v + ave);
    }
}

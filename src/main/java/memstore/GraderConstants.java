package memstore;


public class GraderConstants {
    public static int[] seeds = {0, 1, 2, 3, 4};
    public static int getSeed() {
        return seeds[0];
    }
    public static int getSeed(int seedId) {
        return seeds[seedId];
    }
}

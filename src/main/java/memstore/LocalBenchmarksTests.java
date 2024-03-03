package memstore;

import memstore.benchmarks.*;

public class LocalBenchmarksTests {

    public static void main (String[] args) {
        // Change as per local test
        TableBenchmark benchmark = new ColumnSumNarrowBench();
        try{
            benchmark.prepare();
        } catch (Exception e){
            throw new RuntimeException("Threw Exception");
        }

        long startTime = System.nanoTime();

        // Change as per local test
        long ans = benchmark.testColumnTable();

        long totalTime = System.nanoTime() - startTime;

        System.out.println("Time (ms): " + totalTime);
        System.out.print("Computed Value: " + ans);

    }

}

package memstore.benchmarks;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import memstore.table.ColumnTable;
import memstore.table.IndexedRowTable;
import memstore.table.RowTable;

import java.io.IOException;

/**
 * Queries that perform complex operations on many columns for a large fraction of the rows
 * will benefit from row-based layouts due to memory locality.
 *
 * Note that when the thresholds are not very selective as seen in the parameters used
 * in this test, using indices may not be very effective.
 */
public class PredicatedAllColumnsSumBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;
    int t1;

    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                100_000,
                100
        );
        t1 = 50;

        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(0);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    public long testRowTable() {
        return rt.predicatedAllColumnsSum(t1);
    }

    public long testColumnTable() {
        return ct.predicatedAllColumnsSum(t1);
    }

    public long testIndexedTable() {
        return it.predicatedAllColumnsSum(t1);
    }
}

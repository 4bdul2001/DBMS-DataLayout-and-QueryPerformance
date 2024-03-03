package memstore.benchmarks;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import memstore.table.ColumnTable;
import memstore.table.IndexedRowTable;
import memstore.table.RowTable;

import java.io.IOException;

/**
 * Highly selective predicates should allow indexes to perform very well.
 */
public class PredicatedColumnSumBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;
    int t1, t2;


    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                1_000_000,
                4
        );
        t1 = 500;
        t2 = 10;

        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(2);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    public long testRowTable() {
        return rt.predicatedColumnSum(t1, t2);
    }

    public long testColumnTable() {
        return ct.predicatedColumnSum(t1, t2);
    }

    public long testIndexedTable() {
        return it.predicatedColumnSum(t1, t2);
    }
}

package memstore.benchmarks;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import memstore.table.ColumnTable;
import memstore.table.IndexedRowTable;
import memstore.table.RowTable;

import java.io.IOException;

/**
 * Column scans on tables with many columns should do better on column stores where
 * the values will be stored in contiguous memory.
 */

public class ColumnSumWideBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;

    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                1_000_000,
                20
        );
        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(0);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    public long testRowTable() {
        return rt.columnSum();
    }

    public long testColumnTable() {
        return ct.columnSum();
    }

    public long testIndexedTable() {
        return it.columnSum();
    }
}

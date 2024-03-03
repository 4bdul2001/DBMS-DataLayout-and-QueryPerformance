package memstore.benchmarks;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import memstore.table.ColumnTable;
import memstore.table.IndexedRowTable;
import memstore.table.RowTable;

import java.io.IOException;

/**
 * Full column scans on a narrow table should exhibit similar performance
 * on all table types: there is no way to get around a large fraction of the data.
 */

public class ColumnSumNarrowBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;

    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                1_000_000,
                3
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

package memstore.table;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import memstore.data.ByteFormat;
import memstore.data.DataLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * IndexedRowTable, which stores data in row-major format.
 * That is, data is laid out like
 *   row 1 | row 2 | ... | row n.
 *
 * Also has a tree index on column `indexColumn`, which points
 * to all row indices with the given value.
 */
public class IndexedRowTable implements Table {

    int numCols;
    int numRows;
    private ArrayList<TreeMap<Integer, IntArrayList>> treeMaps;
    private ByteBuffer rows;
    private int indexColumn;

    public IndexedRowTable(int indexColumn) {
        this.indexColumn = indexColumn;
    }

    /**
     * Loads data into the table through passed-in data loader. Is not timed.
     *
     * @param loader Loader to load data from.
     * @throws IOException
     */
    @Override
    public void load(DataLoader loader) throws IOException {
        this.numCols = loader.getNumCols();
        List<ByteBuffer> rows = loader.getRows();
        this.numRows = rows.size();
        this.rows = ByteBuffer.allocate(ByteFormat.FIELD_LEN * numRows * numCols);

        this.treeMaps = new ArrayList<>();
        for(int i=0; i<this.numCols; i++) treeMaps.add(new TreeMap<>());

        for (int rowId = 0; rowId < numRows; rowId++) {
            ByteBuffer curRow = rows.get(rowId);
            for (int colId = 0; colId < numCols; colId++) {
                /*
                    for the map -> key: curRow.getInt(ByteFormat.FIELD_LEN * colId)
                                   val: rowId
                 */
                this.treeMaps.get(colId).putIfAbsent(
                        curRow.getInt(ByteFormat.FIELD_LEN * colId),
                        new IntArrayList()
                );
                this.treeMaps.get(colId).get(curRow.getInt(ByteFormat.FIELD_LEN * colId)).add(rowId);

                // insert to rows ByteBuffer
                int offset = ByteFormat.FIELD_LEN * ((rowId * numCols) + colId);
                this.rows.putInt(offset, curRow.getInt(ByteFormat.FIELD_LEN * colId));
            }
        }
    }

    /**
     * Returns the int field at row `rowId` and column `colId`.
     */
    @Override
    public int getIntField(int rowId, int colId) {
        int offset = ByteFormat.FIELD_LEN * ((rowId * numCols) + colId);
        return rows.getInt(offset);
    }

    /**
     * Inserts the passed-in int field at row `rowId` and column `colId`.
     */
    @Override
    public void putIntField(int rowId, int colId, int field) {
        int offset = ByteFormat.FIELD_LEN * ((rowId * numCols) + colId);
        rows.putInt(offset, field);
        this.treeMaps.get(colId).putIfAbsent(field, new IntArrayList());
        this.treeMaps.get(colId).get(field).add(rowId);
    }

    /**
     * Implements the query
     *  SELECT SUM(col0) FROM table;
     *
     *  Returns the sum of all elements in the first column of the table.
     */
    @Override
    public long columnSum() {
        long sum = 0;
        for (int rowId = 0; rowId < this.numRows; rowId++){
            sum += this.getIntField(rowId, 0);
        }
        return sum;
    }

    /**
     * Implements the query
     *  SELECT SUM(col0) FROM table WHERE col1 > threshold1 AND col2 < threshold2;
     *
     *  Returns the sum of all elements in the first column of the table,
     *  subject to the passed-in predicates.
     */
    @Override
    public long predicatedColumnSum(int threshold1, int threshold2) {
        long sum = 0;

        SortedMap<Integer, IntArrayList> rowsFromCol1 = this.treeMaps.get(1).tailMap(threshold1, false);
        SortedMap<Integer, IntArrayList> rowsFromCol2 = this.treeMaps.get(2).headMap(threshold2, false);

        Set<Integer> rowsToSum = new HashSet<>();
        rowsFromCol1.values().forEach(list -> rowsToSum.addAll(list));

        Set<Integer> col2Rows = new HashSet<>();
        rowsFromCol2.values().forEach(list -> col2Rows.addAll(list));

        rowsToSum.retainAll(col2Rows);

        for (Integer rowId: rowsToSum){
            sum += this.getIntField(rowId, 0);
        }

        return sum;
    }

    /**
     * Implements the query
     *  SELECT SUM(col0) + SUM(col1) + ... + SUM(coln) FROM table WHERE col0 > threshold;
     *
     *  Returns the sum of all elements in the rows which pass the predicate.
     */
    @Override
    public long predicatedAllColumnsSum(int threshold) {
        long sum = 0;
        SortedMap<Integer, IntArrayList> rowsFromCol0 = this.treeMaps.get(0).tailMap(threshold, false);

        for (IntArrayList intList : rowsFromCol0.values()) {
            for (Integer rowId: intList){
                for (int colId = 0; colId < this.numCols; colId++){
                    sum += this.getIntField(rowId, colId);
                }
            }
        }
        return sum;
    }

    /**
     * Implements the query
     *   UPDATE(col3 = col3 + col2) WHERE col0 < threshold;
     *
     *   Returns the number of rows updated.
     */
    @Override
    public int predicatedUpdate(int threshold) {
        int numOfRowsUpdated = 0;
        SortedMap<Integer, IntArrayList> rowsFromCol0 = this.treeMaps.get(0).headMap(threshold, false);

        for (IntArrayList intList : rowsFromCol0.values()) {
            for (Integer rowId: intList){
                int sum = this.getIntField(rowId, 3) + this.getIntField(rowId, 2);
                this.putIntField(rowId, 3, sum);
                numOfRowsUpdated++;
            }
        }

        return numOfRowsUpdated;
    }
}

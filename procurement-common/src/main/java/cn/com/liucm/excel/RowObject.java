package cn.com.liucm.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * excel 行对象
 * 
 * @author liucm
 * @version 1.0
 */
public class RowObject implements Comparable<RowObject> {

    /** The key. */
    String key;

    /** The row. */
    List row = new ArrayList();

    /** The row index. */
    int rowIndex = -1;

    /**
     * Gets the compare index.
     * 
     * @return the compare index
     */
    public List<Integer> getCompareIndex() {
        return compareIndex;
    }

    /**
     * Sets the compare index.
     * 
     * @param compareIndex
     *            the new compare index
     */
    public void setCompareIndex(List<Integer> compareIndex) {
        this.compareIndex = compareIndex;
    }

    /** The compare index. */
    private List<Integer> compareIndex;

    /**
     * Instantiates a new row object.
     */
    public RowObject() {
    }

    /**
     * Gets the row.
     * 
     * @return the row
     */
    public List getRow() {
        return row;
    }

    /**
     * Sets the row.
     * 
     * @param row
     *            the new row
     */
    public void setRow(List row) {
        this.row = row;
    }

    /**
     * Gets the key.
     * 
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     * 
     * @param key
     *            the new key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Adds the.
     * 
     * @param obj
     *            the obj
     */
    public void add(Object obj) {
        row.add(obj);
    }

    /**
     * Adds the all.
     * 
     * @param objList
     *            the obj list
     */
    public void addAll(List objList) {
        row.addAll(objList);
    }

    /**
     * Gets the.
     * 
     * @param index
     *            the index
     * @return the object
     */
    public Object get(int index) {
        return row.get(index);
    }

    /**
     * Removes the.
     * 
     * @param index
     *            the index
     */
    public void remove(int index) {
        row.remove(index);
    }

    /**
     * Adds the.
     * 
     * @param index
     *            the index
     * @param o
     *            the o
     */
    public void add(int index, Object o) {
        row.add(index, o);
    }

    /**
     * Size.
     * 
     * @return the int
     */
    public int size() {
        return row.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        RowObject rowObject = (RowObject) o;

        if (!key.equals(rowObject.key)){
            return false;
        }
        if (!row.equals(rowObject.row)){
            return false;
        }
        return true;
    }

    /**
     * Gets the row index.
     * 
     * @return the row index
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Sets the row index.
     * 
     * @param rowIndex
     *            the new row index
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + row.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(RowObject o) {
        int result = 0;
        if (compareIndex != null && !compareIndex.isEmpty()) {
            for (int i = 0; i < compareIndex.size(); i++) {
                if (size() > (i + 1) && o.size() > (i + 1)) {
                    result = ((Comparable) get(compareIndex.get(i))).compareTo(o.get(compareIndex.get(i)));
                    if (result == 0) {
                        continue;
                    }
                } else {
                    result = -1;
                }
            }

        }
        return result;
    }

}

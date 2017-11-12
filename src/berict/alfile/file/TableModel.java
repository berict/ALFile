package berict.alfile.file;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

import static berict.alfile.Main.DEBUG;

public class TableModel extends AbstractTableModel {
    private Object[][] data = new Object[0][3];
    private ArrayList<Object[]> dataList = new ArrayList<>();

    // example from @link http://www.java2s.com/Code/Java/Swing-JFC/TablewithacustomTableModel.htm
    private String[] columnNames = {
            "Original File names",
            "Changed File names",
            "Location"
    };

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public void add(FileTableItem item) {
        dataList.add(item.toObjects());
        updateFromDataList();

        if (DEBUG) {
            System.out.println("Added value " + item.getFile().getFullPath());
        }
    }

    public void set(int column, FileTableItem item) {
        dataList.set(column, item.toObjects());
        updateFromDataList();
    }

    private void updateFromDataList() {
        // update the data
        data = dataList.toArray(new Object[dataList.size()][3]);
        fireTableDataChanged();
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column
     * would contain text ("true"/"false"), rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }


    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                    + " to " + value + " (an instance of "
                    + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
}

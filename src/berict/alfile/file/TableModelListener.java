package berict.alfile.file;

import berict.alfile.Main;

import javax.swing.event.TableModelEvent;


public class TableModelListener implements javax.swing.event.TableModelListener {

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn(); // should always be 1 (the original)

        if (column == 2) {
            // only capture changed file names column
            TableModel tableModel = (TableModel) e.getSource();
            String cellValue = tableModel.getValueAt(row, column).toString();
            String dataValue = tableModel.get(row).getFile().getFileName();
            if (!cellValue.equals(dataValue)) {
                Main.log("Value changed at table ["
                            + row + ", " + column + "] to "
                            + tableModel.getValueAt(row, column).toString());
            }

            tableModel.get(row).getFile().setName(
                    tableModel.getValueAt(row, column).toString(),
                    true
            );
            tableModel.update();
        }
    }
}

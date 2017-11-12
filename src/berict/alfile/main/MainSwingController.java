package berict.alfile.main;

import berict.alfile.file.FileTableItem;
import lib.FileDrop;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static berict.alfile.Main.DEBUG;

public class MainSwingController {

    private JFrame frame = new JFrame();

    public MainSwingController() {

        frame.setSize(new Dimension(960, 540));
        // this kills the process on exit
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("AlFile");

        ImageIcon img = new ImageIcon("icon.png");
        frame.setIconImage(img.getImage());

        initLeftPane();
        initCenterPane();

        frame.setVisible(true);
    }

    private void initCenterPane() {
        JPanel centerPane = new JPanel();
        centerPane.setSize(740, frame.getHeight());

        TableModel tableModel = new TableModel();

        JTable table = new JTable();
        table.setModel(tableModel);
        table.setSize(500, 300);
        table.setRowHeight(30);

        // Center Align
        DefaultTableCellRenderer align = new DefaultTableCellRenderer();
        align.setHorizontalAlignment(SwingConstants.LEFT);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(align);
        }

        centerPane.add(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(740, 300));
        centerPane.add(scrollPane);
        frame.add(centerPane, BorderLayout.CENTER);

        // drag and drop files
        new FileDrop(System.out, centerPane, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                for (File file : files) {
                    tableModel.add(new FileTableItem(file));
                }
            }
        });
    }

    private void initLeftPane() {
        JPanel leftPane = new JPanel();
        leftPane.setLayout(new GridLayout(11, 1));
        leftPane.setBounds(0, 0, 220, frame.getHeight());
        leftPane.setBackground(new Color(Integer.parseInt("B0BEC5", 16)));

        // add items
        String items[] = {
                "Change file extension",
                "To lower case",
                "To upper case",
                "Replace",
                "Insert first",
                "Insert end",
                "Renumber"
        };

        addItemsToButton(items, leftPane);

        // add radio buttons
        JRadioButton r1 = new JRadioButton("All");
        JRadioButton r2 = new JRadioButton("Selected");
        r1.setBackground(new Color(Integer.parseInt("B0BEC5", 16)));
        r2.setBackground(new Color(Integer.parseInt("B0BEC5", 16)));

        r1.setSelected(true);

        ButtonGroup bg = new ButtonGroup();
        bg.add(r1);
        bg.add(r2);

        JPanel radioPane = new JPanel();
        radioPane.setLayout(new GridLayout(2, 1));
        radioPane.add(r1);
        radioPane.add(r2);

        // add margin
        leftPane.add(new JLabel());
        leftPane.add(new JLabel());

        // add to layout
        leftPane.add(radioPane);

        // add process button
        JButton process = new JButton("Process");
        leftPane.add(process);

        frame.setLayout(new BorderLayout());
        frame.add(leftPane, BorderLayout.WEST);
    }

    private void addItemsToButton(String items[], JPanel panel) {
        for (String item : items) {
            panel.add(new JButton(item));
        }
    }

    static class TableModel extends AbstractTableModel {

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
}

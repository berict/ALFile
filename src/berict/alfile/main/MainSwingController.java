package berict.alfile.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import lib.FileDrop;

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

        // get files
        String path = "C://";
        File dirFile = new File(path);
        File[] fileList = dirFile.listFiles();

        String[] columnNames = {
                "Original File names",
                "Changed File names",
                "Location"
        };

        // get files count
        int fileCount = 0;
        for (int i = 0; i < fileList.length; i++) {
        	if (fileList[i].isFile())
        		fileCount ++;
        }
        
        Object[][] data = new Object[fileCount][3];

        // set file table
        int fileNum = 0;
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile()) {
                data[fileNum][0] = fileList[i].getName();
                data[fileNum][2] = fileList[i].getAbsolutePath();
                fileNum++;
            }
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // only make the 2nd column editable
                return column == 1;
            }
        };

        JTable table = new JTable();
        table.setModel(tableModel);
        table.setSize(500, 300);
        table.setRowHeight(30);

        // Center Align
        DefaultTableCellRenderer defalutCellRenderer = new DefaultTableCellRenderer();
        defalutCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel cloumnModel = table.getColumnModel();
        for (int i = 0; i < cloumnModel.getColumnCount(); i++)
            cloumnModel.getColumn(i).setCellRenderer(defalutCellRenderer);

        centerPane.add(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(740, 300));
        centerPane.add(scrollPane);
        frame.add(centerPane, BorderLayout.CENTER);
        
        // drag and drop files
        new FileDrop(System.out, table, new FileDrop.Listener() {   
        	public void filesDropped( java.io.File[] files ) {
        		for (int i = 0; i < files.length; i++) {
        			String fileName = files[i].getName();
        			data[0][0] = fileName;
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

    void addItemsToButton(String items[], JPanel panel) {
        for (String item : items) {
            panel.add(new JButton(item));
        }
    }
}

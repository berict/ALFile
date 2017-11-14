package berict.alfile.main;

import berict.alfile.file.FileTableItem;
import berict.alfile.file.TableModel;
import lib.FileDrop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.File;

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

        // center align
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
}

package berict.alfile.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MainController {
	
	private JFrame frame = new JFrame();

	public MainController() {

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
	
	void initCenterPane() {
		JPanel centerPane = new JPanel();
//		centerPane.setLayout(new GridLayout(1, 3));
		centerPane.setSize(740, frame.getHeight());
		
//		String[] originalFilenames = new String[99];
//		
//		JTable t1 = new JTable();
//		JTableHeader header = new JT
//		t1.setTableHeader(null);
		
		String[] columnNames = {
				"Original File names",
				"Changed File names",
				"Location"
                };

		Object[][] data = new Object[99][3];

		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
			
			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 1) {
					return true;
				} else {
					return false;
				}
			}
			
		};
		
		JTable table = new JTable();
		table.setModel(tableModel);
		
		centerPane.add(table);
		centerPane.add(new JScrollPane(table));
		frame.add(centerPane, BorderLayout.CENTER);
	}
	
	void initLeftPane() {
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

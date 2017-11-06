package berict.alfile.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MainController {

	public MainController() {
		JFrame frame = new JFrame();

		frame.setSize(new Dimension(960, 540));
		// this kills the process on exit
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("AlFile");

		ImageIcon img = new ImageIcon("icon.png");
		frame.setIconImage(img.getImage());

		JPanel leftPane = new JPanel();
		leftPane.setLayout(new GridLayout(11, 1));
		leftPane.setBounds(0, 0, 220, frame.getHeight());
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
		JRadioButton r1 = new JRadioButton("All");
		JRadioButton r2 = new JRadioButton("Selected");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(r1);
		bg.add(r2);

		JPanel radioPane = new JPanel();
		radioPane.setLayout(new GridLayout(2, 1));
		radioPane.add(r1);
		radioPane.add(r2);

		leftPane.add(radioPane);

		frame.setLayout(new BorderLayout());
		frame.add(leftPane, BorderLayout.WEST);
		frame.setVisible(true);
	}

	void addItemsToButton(String items[], JPanel panel) {
		for (String item : items) {
			panel.add(new JButton(item));
		}
	}
}

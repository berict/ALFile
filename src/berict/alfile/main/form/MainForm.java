package berict.alfile.main.form;

import berict.alfile.file.FileTableItem;
import berict.alfile.file.TableModel;
import lib.FileDrop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainForm extends JFrame {

    private JPanel parentPanel;// = new JPanel();
    private JPanel leftPanel;
    private JButton replaceButton;
    private JButton changeCaseButton;
    private JButton insertButton;
    private JButton changeExtensionButton;
    private JButton renumberButton;
    private JRadioButton processAllButton;
    private JRadioButton processSelectedButton;
    private JButton processButton;
    private JPanel centerPanel;
    private JTable table;

    private ButtonGroup radioGroup;

    public static TableModel tableModel;

    public static int WINDOW_WIDTH = 960;
    public static int WINDOW_HEIGHT = 540;

    public MainForm() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setContentPane(parentPanel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AlFile");

        ImageIcon img = new ImageIcon("icon.png");
        setIconImage(img.getImage());

        try {
            // local theme
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("ok");
        }
        setUI();
    }

    private void setUI() {
        tableModel = new TableModel();
        table.setModel(tableModel);
        table.setSize(500, 300);
        table.setRowHeight(30);

        radioGroup = new ButtonGroup();
        radioGroup.add(processAllButton);
        radioGroup.add(processSelectedButton);

        // center align
        DefaultTableCellRenderer align = new DefaultTableCellRenderer();
        align.setHorizontalAlignment(SwingConstants.LEFT);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(align);
        }

        // drag and drop files
        new FileDrop(System.out, centerPanel, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                for (File file : files) {
                    tableModel.add(new FileTableItem(file));
                }
            }
        });

        replaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField oldString = new JTextField();
                JTextField newString = new JTextField();
                final JComponent[] inputs = new JComponent[]{
                        new JLabel("String to replace"),
                        oldString,
                        new JLabel("New string"),
                        newString
                };
                int result = JOptionPane.showConfirmDialog(null, inputs, "Replace", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    System.out.println(oldString.getText() + " > to > " + newString.getText());
                } else {
                    System.out.println("Result : " + result);
                }

                if (table.getSelectedRows().length > 0) {
                    if (oldString != null && newString != null) {
                        for (int i : table.getSelectedRows()) {
                            tableModel.get(i)
                                    .getFile()
                                    .replaceAll(oldString.getText(), newString.getText());
                        }
                    } else {

                    }
                } else {
                    final JComponent[] label = new JComponent[]{
                            new JLabel("No file was selected. Do you want to apply to all files?")
                    };
                    if (JOptionPane.showConfirmDialog(null, label, "Error", JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        // apply to all
                        for (int i = 0; i < table.getRowCount(); i++) {
                            tableModel.get(i)
                                    .getFile()
                                    .replaceAll(oldString.getText(), newString.getText());
                        }
                    } else {
                        System.out.println("Result : " + result);
                    }
                }
            }
        });

        changeExtensionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField newExtension = new JTextField();
                final JComponent[] inputs = new JComponent[]{
                        new JLabel("New extension"),
                        newExtension
                };
                int result = JOptionPane.showConfirmDialog(null, inputs, "Replace", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    System.out.println("Change extension to " + newExtension);
                } else {
                    System.out.println("Result : " + result);
                }

                if (newExtension != null) {
                    // TODO add actions to the table
                }
            }
        });

        changeCaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // center align -- failed
                JLabel label = new JLabel("Select Option", SwingConstants.CENTER);
                String[] options = new String[]{ "to Uppercase", "to Lowercase", "cancel" };
                String title = "Change case";
                String msg = "Select the option";
                int result = JOptionPane.showOptionDialog(label, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "cancel");
                int row = table.getSelectedRow();
                String oldString = table.getValueAt(row, 0).toString();
                if (result == 0) {
                    tableModel.setValueAt(oldString.toUpperCase(), row, 1);
                } else if (result == 1) {
                    tableModel.setValueAt(oldString.toLowerCase(), row, 1);
                } else {
                    // cancel
                }
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField insertString = new JTextField();
                final JComponent[] inputs = new JComponent[]{
                        new JLabel("Insert String : "),
                        insertString
                };
                String[] options = new String[] { "Insert to beginning", "Insert to end", "cancel" };
                // TODO add input option
                String title = "Insert String";
                int result = JOptionPane.showOptionDialog(null, inputs, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "cancel");
                int row = table.getSelectedRow();
                String oldString = table.getValueAt(row, 0).toString();
                if (result == 0) {
                    if (insertString != null) {
                        table.setValueAt(insertString + oldString, row, 1);
                        System.out.println("Result : " + insertString + oldString);
                    } else {

                    }
                } else if (result == 1) {
                    if (insertString != null) {
                        table.setValueAt(oldString + insertString, row, 1);
                        System.out.println("Result : " + oldString + insertString);
                    } else {

                    }
                }
            }
        });

        renumberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO get Selected file's names and numbering them
            }
        });

        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (processAllButton.isSelected()){
                    // TODO process All Event
                    System.out.println("Process : processAll is Selected");
                } else if (processSelectedButton.isSelected()) {
                    // TODO process Selected Event
                    System.out.println("Process : processSelected is Selected");
                } else {
                    String[] options = new String[]{ "process All", "process Selected only", "cancel" };
                    String title = "No process option";
                    String msg = "There is no process option. Would you like to select it now?";
                    int result = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "cancel");
                    if (result == JOptionPane.YES_OPTION) {
                        // TODO process All Event
                    } else if (result == JOptionPane.NO_OPTION) {
                        // TODO process Selected Event
                    }
                    System.out.println("Process : Nothing Selected");
                }
            }
        });
    }

    private void makeErrorDialog(){

    }

    private void makeDialog(String title, String text, int optionType, int confirmAction, Runnable onConfirm, Runnable onCancel) {
        final JComponent[] label = new JComponent[]{ new JLabel(text) };
        if (JOptionPane.showConfirmDialog(null, label, title, optionType)
                == confirmAction) {
            // apply to all
            onConfirm.run();
        } else {
            onCancel.run();
        }
    }
}

package berict.alfile.main.form;

import berict.alfile.file.FileTableItem;
import berict.alfile.file.TableModel;
import berict.alfile.file.TableModelListener;
import lib.FileDrop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static berict.alfile.Main.DEBUG;
import static javax.swing.JOptionPane.*;

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
            e.printStackTrace();
            makeErrorAlert("Can't get system theme, using default theme.");
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
        processAllButton.setSelected(true);

        // center align
        DefaultTableCellRenderer align = new DefaultTableCellRenderer();
        align.setHorizontalAlignment(SwingConstants.LEFT);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(align);
        }

        tableModel.addTableModelListener(new TableModelListener());

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
                if (table.getRowCount() > 0) {
                    // table has content
                    JTextField oldString = new JTextField();
                    JTextField newString = new JTextField();

                    ButtonGroup buttonGroup = new ButtonGroup();
                    JRadioButton all = new JRadioButton("Replace all match");
                    JRadioButton first = new JRadioButton("Replace only first match");

                    buttonGroup.add(all);
                    buttonGroup.add(first);
                    all.setSelected(true);

                    final JComponent[] inputs = new JComponent[]{
                            new JLabel("String to replace"),
                            oldString,
                            new JLabel("New string"),
                            newString,
                            all, first
                    };

                    makeDialog("Replace", inputs, OK_CANCEL_OPTION, OK_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (oldString.getText() != null && newString.getText() != null) {
                                        // all filled
                                        if (table.getSelectedRows().length > 0) {
                                            // has selected rows
                                            if (all.isSelected()) {
                                                // replace all
                                                for (int row : table.getSelectedRows()) {
                                                    tableModel.get(row)
                                                            .getFile()
                                                            .replaceAll(oldString.getText(), newString.getText());
                                                }
                                            } else {
                                                // replace first match
                                                for (int row : table.getSelectedRows()) {
                                                    tableModel.get(row)
                                                            .getFile()
                                                            .replaceFirst(oldString.getText(), newString.getText());
                                                }
                                            }
                                            tableModel.update();
                                        } else {
                                            // doesn't have selected rows
                                            makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (all.isSelected()) {
                                                                // replace all
                                                                for (int row = 0; row < table.getRowCount(); row++) {
                                                                    tableModel.get(row)
                                                                            .getFile()
                                                                            .replaceAll(oldString.getText(), newString.getText());
                                                                }
                                                            } else {
                                                                // replace first match
                                                                for (int row = 0; row < table.getRowCount(); row++) {
                                                                    tableModel.get(row)
                                                                            .getFile()
                                                                            .replaceFirst(oldString.getText(), newString.getText());
                                                                }
                                                            }
                                                            tableModel.update();
                                                        }
                                                    }, null);
                                        }
                                    } else {
                                        makeErrorAlert("Fill out to continue");
                                    }
                                }
                            }, null);
                } else {
                    makeErrorAlert("No file found");
                }
            }
        });

        changeExtensionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    // table has content
                    JTextField newExtension = new JTextField();
                    final JComponent[] inputs = new JComponent[]{
                            new JLabel("New extension"),
                            newExtension
                    };

                    makeDialog("Change extension", inputs, OK_CANCEL_OPTION, OK_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (newExtension.getText() != null) {
                                        // all filled
                                        if (table.getSelectedRows().length > 0) {
                                            // has selected rows
                                            for (int row : table.getSelectedRows()) {
                                                tableModel.get(row)
                                                        .getFile()
                                                        .replaceExtension(newExtension.getText());
                                            }
                                            tableModel.update();
                                        } else {
                                            // doesn't have selected rows
                                            makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int row = 0; row < table.getRowCount(); row++) {
                                                                tableModel.get(row)
                                                                        .getFile()
                                                                        .replaceExtension(newExtension.getText());
                                                            }
                                                            tableModel.update();
                                                        }
                                                    }, null);
                                        }
                                    } else {
                                        makeErrorAlert("Fill out to continue");
                                    }
                                }
                            }, null);
                } else {
                    makeErrorAlert("No file found");
                }
            }
        });

        changeCaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    // table has content

                    ButtonGroup buttonGroup = new ButtonGroup();
                    JRadioButton upper = new JRadioButton("Rename to UPPERCASE");
                    JRadioButton lower = new JRadioButton("Rename to lowercase");

                    buttonGroup.add(upper);
                    buttonGroup.add(lower);
                    upper.setSelected(true);

                    final JComponent[] inputs = new JComponent[]{
                            new JLabel("New extension"),
                            upper,
                            lower
                    };

                    makeDialog("Change case", inputs, OK_CANCEL_OPTION, OK_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    boolean isUpper = upper.isSelected();
                                    if (table.getSelectedRows().length > 0) {
                                        // has selected rows
                                        for (int row : table.getSelectedRows()) {
                                            tableModel.get(row)
                                                    .getFile()
                                                    .changeCase(isUpper);
                                        }
                                        tableModel.update();
                                    } else {
                                        // doesn't have selected rows
                                        makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (int row = 0; row < table.getRowCount(); row++) {
                                                            tableModel.get(row)
                                                                    .getFile()
                                                                    .changeCase(isUpper);
                                                        }
                                                        tableModel.update();
                                                    }
                                                }, null);
                                    }
                                }
                            }, null);

                } else {
                    makeErrorAlert("No file found");
                }
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    // table has content
                    ButtonGroup buttonGroup = new ButtonGroup();
                    JRadioButton front = new JRadioButton("Insert to front");
                    JRadioButton back = new JRadioButton("Insert to back");

                    JCheckBox containExtension = new JCheckBox("Include extensions");
                    containExtension.setSelected(false);

                    buttonGroup.add(front);
                    buttonGroup.add(back);
                    front.setSelected(true);

                    JTextField insertString = new JTextField();

                    final JComponent[] inputs = new JComponent[]{
                            new JLabel("String to insert"),
                            insertString,
                            front,
                            back,
                            containExtension
                    };

                    makeDialog("Insert", inputs, OK_CANCEL_OPTION, OK_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println(insertString.getText());
                                    if (insertString.getText() != null) {
                                        boolean isFront = front.isSelected();
                                        boolean isContainExtension = containExtension.isSelected();
                                        if (table.getSelectedRows().length > 0) {
                                            // has selected rows
                                            for (int row : table.getSelectedRows()) {
                                                tableModel.get(row)
                                                        .getFile()
                                                        .insert(insertString.getText(), isFront, isContainExtension);
                                            }
                                            tableModel.update();
                                        } else {
                                            // doesn't have selected rows
                                            makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int row = 0; row < table.getRowCount(); row++) {
                                                                tableModel.get(row)
                                                                        .getFile()
                                                                        .insert(insertString.getText(), isFront, isContainExtension);
                                                            }
                                                            tableModel.update();
                                                        }
                                                    }, null);
                                        }
                                    } else {
                                        makeErrorAlert("Fill out to continue");
                                    }
                                }
                            }, null);

                } else {
                    makeErrorAlert("No file found");
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
                if (processAllButton.isSelected()) {
                    if (DEBUG) {
                        System.out.println("process all");
                    }
                    for (int row = 0; row < table.getRowCount(); row++) {
                        tableModel.get(row)
                                .getFile()
                                .apply();
                    }
                } else if (processSelectedButton.isSelected()) {
                    if (DEBUG) {
                        System.out.println("process selected");
                    }
                    for (int row : table.getSelectedRows()) {
                        tableModel.get(row)
                                .getFile()
                                .apply();
                    }
                }
            }
        });
    }

    private void makeDialog(String title, Object message,
                            int optionType, int confirmAction,
                            Runnable onConfirm, Runnable onCancel) {
        showDialog(title, message, PLAIN_MESSAGE, optionType, confirmAction, onConfirm, onCancel);
    }

    private void makeWarningDialog(Object message,
                                   int optionType, int confirmAction,
                                   Runnable onConfirm, Runnable onCancel) {
        showDialog("Warning", message, WARNING_MESSAGE, optionType, confirmAction, onConfirm, onCancel);
    }

    private void makeCustomDialog(String title, Object message, int messageType,
                                  int optionType, int confirmAction,
                                  Runnable onConfirm, Runnable onCancel) {
        showDialog(title, message, messageType, optionType, confirmAction, onConfirm, onCancel);
    }

    private void makeErrorAlert(String text) {
        JOptionPane.showMessageDialog(null, text, "Error", ERROR_MESSAGE);
    }

    private void makeAlert(String title, String text, int messageType,
                           int optionType, int confirmAction,
                           Runnable onConfirm, Runnable onCancel) {
        final JComponent[] label = new JComponent[]{new JLabel(text)};
        showDialog(title, label, messageType, optionType, confirmAction, onConfirm, onCancel);
    }

    private void showDialog(String title, Object message, int messageType,
                            int optionType, int confirmAction,
                            Runnable onConfirm, Runnable onCancel) {
        if (JOptionPane.showConfirmDialog(null, message, title, optionType, messageType)
                == confirmAction) {
            if (onConfirm != null) {
                onConfirm.run();
            }
        } else {
            if (onCancel != null) {
                onCancel.run();
            }
        }
    }
}

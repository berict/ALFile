package berict.alfile.main.form;

import berict.alfile.file.FileTableItem;
import berict.alfile.file.TableModel;
import berict.alfile.file.TableModelListener;
import berict.alfile.file.TableModelRenderer;
import lib.FileDrop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

import static berict.alfile.Main.DEBUG;
import static berict.alfile.file.File.*;
import static javax.swing.JOptionPane.*;

public class MainForm extends JFrame {

    private JPanel parentPanel;// = new JPanel();
    private JPanel leftPanel;
    private JButton replaceButton;
    private JButton changeCaseButton;
    private JButton insertButton;
    private JButton changeExtensionButton;
    private JButton advancedButton;
    private JRadioButton processAllButton;
    private JRadioButton processSelectedButton;
    private JButton processButton;
    private JPanel centerPanel;
    private JTable table;
    private JButton subfolderButton;
    private JButton exportContentButton;
    private JPanel statusPanel;
    private JLabel status;

    private ButtonGroup radioGroup;

    public static TableModel tableModel;

    public static int WINDOW_WIDTH = 960;
    public static int WINDOW_HEIGHT = 540;

    public static int WINDOW_MIN_WIDTH = 600;
    public static int WINDOW_MIN_HEIGHT = 420;

    public MainForm() {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT));
        setContentPane(parentPanel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int modified = tableModel.getModifiedCount();
                if (modified > 0) {
                    String text;
                    if (modified == 1) {
                        text = "1 file is not processed. Proceed exit?";
                    } else {
                        text = modified + " files not processed. Proceed exit?";
                    }
                    makeCustomDialog("Warning", text, WARNING_MESSAGE,
                            YES_NO_OPTION, YES_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                                }
                            });
                } else {
                    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                }
            }
        });
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
        table.getColumn("Type").setMaxWidth(48);

        radioGroup = new ButtonGroup();
        radioGroup.add(processAllButton);
        radioGroup.add(processSelectedButton);
        processAllButton.setSelected(true);

        statusPanel = new JPanel();
        status = new JLabel("0 items", JLabel.LEFT);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(status, BorderLayout.WEST);
        statusPanel.setBorder(new EmptyBorder(2, 8, 2, 0));
        add("South", statusPanel);

        // add styles
        TableModelRenderer renderer = new TableModelRenderer(new Runnable() {
            @Override
            public void run() {
                refreshStatus();
            }
        });
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(renderer);
        }

        final int[] start = new int[1];
        final int[] end = new int[1];

        table.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    start[0] = table.rowAtPoint(e.getPoint());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                end[0] = table.rowAtPoint(e.getPoint());
                if ((start[0] >= 0 && start[0] < table.getRowCount()) &&
                        (end[0] >= 0 && end[0] < table.getRowCount())) {
                    if (start[0] <= end[0]) {
                        table.setRowSelectionInterval(start[0], end[0]);
                        if (DEBUG) {
                            System.out.println("start = " + start[0] + " / end = " + end[0]);
                        }
                    } else {
                        table.setRowSelectionInterval(end[0], start[0]);
                        if (DEBUG) {
                            System.out.println("start = " + end[0] + " / end = " + start[0]);
                        }
                    }
                } else {
                    table.clearSelection();
                }

                int row[] = table.getSelectedRows();
                if (row.length <= 0) {
                    return;
                }

                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu("Edit");
                    JMenuItem revert = new JMenuItem("Revert changes");
                    JMenuItem remove = new JMenuItem("Remove from list");
                    JMenuItem process = new JMenuItem("Process");

                    if (tableModel.isModified(row)) {
                        revert.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                for (int index : row) {
                                    if (tableModel.get(index).isModified()) {
                                        tableModel.get(index).getFile().revert();
                                        if (DEBUG) {
                                            System.out.println("Menu.revert index=" + index);
                                        }
                                    }
                                }
                                tableModel.update();
                            }
                        });
                        process.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                for (int index : row) {
                                    if (tableModel.get(index).isModified()) {
                                        if (!tableModel.get(index).getFile().apply(tableModel)) {
                                            makeErrorAlert("Failed to process");
                                        }
                                        if (DEBUG) {
                                            System.out.println("Menu.process index=" + index);
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        process.setEnabled(false);
                        revert.setEnabled(false);
                    }

                    remove.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            for (int i = row.length - 1; i >= 0; i--) {
                                int index = row[i];
                                tableModel.remove(index);
                                if (DEBUG) {
                                    System.out.println("Menu.remove index=" + index);
                                }
                            }
                        }
                    });

                    popup.add(revert);
                    popup.add(remove);

                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        // windows only feature
                        JMenuItem open = new JMenuItem("Show in explorer");
                        final boolean[] allExist = {true};
                        open.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (row.length > 1) {
                                    for (int index : row) {
                                        if (!tableModel.get(index).exists()) {
                                            allExist[0] = false;
                                            // found not existing file, break
                                            break;
                                        }
                                    }
                                    if (allExist[0]) {
                                        makeCustomDialog("Warning", new JLabel("This will open multiple windows. Continue?"), WARNING_MESSAGE, YES_NO_OPTION, YES_OPTION,
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (int index : row) {
                                                            showInExplorer(tableModel.get(index).getFile().getFullPath());
                                                        }
                                                    }
                                                }, null);
                                    }
                                } else {
                                    allExist[0] = tableModel.get(row[0]).exists();
                                    if (allExist[0]) {
                                        showInExplorer(tableModel.get(row[0]).getFile().getFullPath());
                                    }
                                }
                            }
                        });
                        open.setEnabled(allExist[0]);
                        popup.add(open);
                    }

                    popup.add(process);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        tableModel.addTableModelListener(new TableModelListener());

        // drag and drop files
        new FileDrop(System.out, centerPanel, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                int duplicateCount = 0;
                for (java.io.File file : files) {
                    if (tableModel.search(file.getAbsolutePath()) < 0) {
                        tableModel.add(new FileTableItem(file));
                    } else {
                        // duplicate
                        ++duplicateCount;
                    }
                }
                if (duplicateCount > 0) {
                    if (duplicateCount == 1) {
                        makeErrorAlert("A duplicate was found");
                    } else {
                        makeErrorAlert(duplicateCount + " duplicates were found");
                    }
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

                    JPanel regexHelp = new JPanel();
                    regexHelp.setLayout(new BorderLayout());
                    regexHelp.add(getRegexHelp(newString), BorderLayout.WEST);
                    JLabel helpText = new JLabel("Click to append expression.");
                    helpText.setBorder(new EmptyBorder(4, 0, 8, 0));
                    regexHelp.add(helpText, BorderLayout.SOUTH);

                    final JComponent[] inputs = new JComponent[]{
                            regexHelp,
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
                                        if (isAvailableForFileName(newString.getText())) {
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
                                            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
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
                                        if (isAvailableForFileName(newExtension.getText())) {
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
                                            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
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
                                        if (isAvailableForFileName(insertString.getText())) {
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
                                            makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
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

        JPopupMenu popup = new JPopupMenu();
        JMenuItem replaceWhitespace = new JMenuItem("Replace whitespace"); // #14
        JMenuItem pathToFileName = new JMenuItem("Path to file name"); // #13
        JMenuItem toAscii = new JMenuItem("File name to ASCII"); // #15

        replaceWhitespace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField textField = new JTextField();
                textField.setText("_");
                JComponent inputs[] = new JComponent[]{
                        new JLabel("New whitespace character"),
                        textField
                };

                makeDialog(replaceWhitespace.getText(), inputs, OK_CANCEL_OPTION, OK_OPTION,
                        new Runnable() {
                            @Override
                            public void run() {
                                String input = textField.getText();
                                if (input != null) {
                                    if (isAvailableForFileName(input)) {
                                        if (input.equals(" ")) {
                                            // no changes
                                            makeErrorAlert("No changes found");
                                        } else {
                                            if (table.getSelectedRows().length > 0) {
                                                // has selected rows
                                                for (int row : table.getSelectedRows()) {
                                                    tableModel.get(row)
                                                            .getFile()
                                                            .replaceAll(" ", input);
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
                                                                            .replaceAll(" ", input);
                                                                }
                                                                tableModel.update();
                                                            }
                                                        }, null);
                                            }
                                        }
                                    } else {
                                        makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
                                    }
                                } else {
                                    makeErrorAlert("Fill out to continue");
                                }
                            }
                        }, null);
            }
        });

        pathToFileName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField textField = new JTextField();
                textField.setText("#");

                JRadioButton insertToStart = new JRadioButton("Insert parent folder name to start");
                JRadioButton replaceAll = new JRadioButton("Replace entire file name");
                insertToStart.setSelected(true);

                ButtonGroup buttonGroup = new ButtonGroup();
                buttonGroup.add(insertToStart);
                buttonGroup.add(replaceAll);

                JComponent inputs[] = new JComponent[]{
                        new JLabel("Directory separator"),
                        textField,
                        insertToStart,
                        replaceAll
                };

                makeDialog(pathToFileName.getText(), inputs, OK_CANCEL_OPTION, OK_OPTION,
                        new Runnable() {
                            @Override
                            public void run() {
                                String directorySeparator = textField.getText();
                                boolean isInsertToStart = insertToStart.isSelected();
                                if (isAvailableForFileName(directorySeparator)) {
                                    if (table.getSelectedRows().length > 0) {
                                        // has selected rows
                                        for (int row : table.getSelectedRows()) {
                                            FileTableItem item = tableModel.get(row);
                                            if (isInsertToStart) {
                                                item.getFile().insertAtStart(item.getFile().getParentFile().getName() + directorySeparator);
                                            } else {
                                                // replace all text to the path
                                                item.getFile().setName(
                                                        item.getFile().getFullPath().replace(
                                                                SEPARATOR, directorySeparator
                                                        ), true
                                                );
                                            }
                                            tableModel.set(row, item);
                                        }
                                        tableModel.update();
                                    } else {
                                        // doesn't have selected rows
                                        makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (int row = 0; row < table.getRowCount(); row++) {
                                                            FileTableItem item = tableModel.get(row);
                                                            if (isInsertToStart) {
                                                                item.getFile().insertAtStart(item.getFile().getParentFile().getName() + directorySeparator);
                                                            } else {
                                                                // replace all text to the path
                                                                item.getFile().setName(
                                                                        item.getFile().getFullPath().replace(
                                                                                SEPARATOR, directorySeparator
                                                                        ), true
                                                                );
                                                            }
                                                            tableModel.set(row, item);
                                                        }
                                                        tableModel.update();
                                                    }
                                                }, null);
                                    }
                                } else {
                                    makeErrorAlert("Following characters are not available for file names: " + RESTRICTED_CHARACTER);
                                }
                            }
                        }, null);
            }
        });

        toAscii.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeWarningDialog("This will remove non-ASCII characters. Continue?", YES_NO_OPTION, YES_OPTION,
                        new Runnable() {
                            @Override
                            public void run() {
                                if (table.getSelectedRows().length > 0) {
                                    // has selected rows
                                    for (int row : table.getSelectedRows()) {
                                        tableModel.get(row).getFile().replaceAll("\\P{Print}", "");
                                    }
                                    tableModel.update();
                                } else {
                                    // doesn't have selected rows
                                    makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (int row = 0; row < table.getRowCount(); row++) {
                                                        tableModel.get(row).getFile().replaceAll("\\P{Print}", "");
                                                    }
                                                    tableModel.update();
                                                }
                                            }, null);
                                }
                            }
                        }, null);
            }
        });

        popup.add(replaceWhitespace);
        popup.add(pathToFileName);
        popup.add(toAscii);

        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.show(advancedButton, advancedButton.getWidth(), 0);
            }
        });

        subfolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    makeWarningDialog("This will move subfolder contents to selected folder. Continue?", YES_NO_OPTION, YES_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (table.getSelectedRows().length > 0) {
                                        // has selected rows
                                        for (int row : table.getSelectedRows()) {
                                            tableModel.get(row).getFile().replaceAll("\\P{Print}", "");
                                        }
                                        tableModel.update();
                                    } else {
                                        // doesn't have selected rows
                                        makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        for (int row = 0; row < table.getRowCount(); row++) {
                                                            tableModel.get(row).getFile().replaceAll("\\P{Print}", "");
                                                        }
                                                        tableModel.update();
                                                    }
                                                }, null);
                                    }
                                }
                            }, null);
                } else {
                    makeErrorAlert("No folder found");
                }
            }
        });

        exportContentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // # 12
            }
        });

        processButton.setMinimumSize(new Dimension(processButton.getWidth(), 36));
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    int processCount = 0;
                    if (processAllButton.isSelected()) {
                        if (!tableModel.hasDuplicate()) {
                            if (DEBUG) {
                                System.out.println("process all");
                            }
                            for (int row = 0; row < table.getRowCount(); row++) {
                                process(row);
                                processCount++;
                            }
                        } else {
                            makeErrorAlert("Duplicated file name found");
                        }
                    } else if (processSelectedButton.isSelected()) {
                        if (!tableModel.hasDuplicate(table.getSelectedRow())) {
                            if (DEBUG) {
                                System.out.println("process selected");
                            }
                            for (int row : table.getSelectedRows()) {
                                process(row);
                                processCount++;
                            }
                        } else {
                            makeErrorAlert("Duplicated file name found");
                        }
                    }
                    if (DEBUG) {
                        System.out.println("Processed " + processCount);
                    }
                    if (processCount == 1) {
                        setStatus("Processed 1 file");
                    } else if (processCount > 1) {
                        setStatus("Processed " + processCount + " files");
                    }
                } else {
                    makeErrorAlert("No file found");
                }
            }
        });
    }

    private void setStatus(String text) {
        status.setText(text);
    }

    private void refreshStatus() {
        int fileCount = tableModel.size();
        int modifiedCount = tableModel.getModifiedCount();
        int selectedCount = table.getSelectedRowCount();

        String text;

        if (fileCount < 1) {
            text = "0 items    ";
        } else if (fileCount > 1) {
            text = fileCount + " items    ";
        } else {
            text = "1 items    ";
        }

        if (selectedCount > 1) {
            text += selectedCount + " items selected    ";
        } else if (selectedCount == 1) {
            text += "1 item selected    ";
        }

        if (modifiedCount > 1) {
            text += modifiedCount + " items modified";
        } else if (modifiedCount == 1) {
            text += "1 item modified";
        }
        status.setText(text);
    }

    private boolean process(int row) {
        if (tableModel.get(row)
                .getFile()
                .apply(tableModel)) {
            // TODO add things
            return true;
        } else {
            return false;
        }
    }

    private JComponent getRegexHelp(JTextField textTarget) {
        String tabTitles[] = {
                "Character",
                "Predefined character",
                "Boundary matches",
                "Groups & Back references",
                "Logical operations & Quantifiers"
        };

        String regex[][][] = {
                {
                        {"[abc]", "matches a or b, or c"},
                        {"[^abc]", "negation, matches everything except a, b, or c"},
                        {"[a-c]", "range, matches a or b, or c"},
                        {"[a-c[f-h]]", "union, matches a, b, c, f, g, h"},
                        {"[a-c&&[b-c]]", "intersection, matches b or c"},
                        {"[a-c&&[^b-c]]", "subtraction, matches a"},
                },
                {
                        {".", "Any character"},
                        {"\\d", "A digit: [0-9]"},
                        {"\\D", "A non-digit: [^0-9]"},
                        {"\\s", "A whitespace character: [ \\t\\n\\x0B\\f\\r]"},
                        {"\\S", "A non-whitespace character: [^\\s]"},
                        {"\\w", "A word character: [a-zA-Z_0-9]"},
                        {"\\W", "A non-word character: [^\\w]"},
                },
                {
                        {"^", "The beginning of a line"},
                        {"$", "The end of a line"},
                        {"\\b", "A word boundary"},
                        {"\\B", "A non-word boundary"},
                        {"\\A", "The beginning of the input"},
                        {"\\G", "The end of the previous match"},
                        {"\\Z", "The end of the input but for the final terminator, if any"},
                        {"\\z", "The end of the input"},
                },
                {
                        {"(...)", "defines a group"},
                        {"\\N", "refers to a matched group"},
                        {"(\\d\\d)", "a group of two digits"},
                        {"(\\d\\d)/\\1", "two digits repeated twice"},
                        {"\\1", "refers to the matched group"},
                },
                {
                        {"XY", "X then Y"},
                        {"X|Y", "X or Y"},
                        {"X?", "X, once or not at all"},
                        {"X*", "X, zero or more times"},
                        {"X+", "X, one or more times"},
                        {"X{n}", "X, exactly n times"},
                        {"X{n,}", "X, at least n times"},
                        {"X{n,m}", "X, at least n but not more than m times"},
                }
        };

        JTabbedPane tabbedPane = new JTabbedPane();

        for (int i = 0; i < tabTitles.length; i++) {
            JPanel panel = new JPanel(false);
            panel.setLayout(new GridLayout(regex[i].length, 2));

            for (int j = 0; j < regex[i].length; j++) {
                JLabel expression = new JLabel(regex[i][j][0]);
                expression.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        textTarget.setText(textTarget.getText() + " " + expression.getText());
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                panel.add(expression);
                panel.add(new JLabel(regex[i][j][1]));
            }

            tabbedPane.addTab(tabTitles[i], panel);
        }

        return tabbedPane;
    }

    private void showInExplorer(String path) {
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + path);
        } catch (Exception ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            makeErrorAlert("Can't show in explorer");
        }
    }

    public static void makeDialog(String title, Object message,
                                  int optionType, int confirmAction,
                                  Runnable onConfirm, Runnable onCancel) {
        showDialog(title, message, PLAIN_MESSAGE, optionType, confirmAction, onConfirm, onCancel);
    }

    public static void makeWarningDialog(Object message,
                                         int optionType, int confirmAction,
                                         Runnable onConfirm, Runnable onCancel) {
        showDialog("Warning", message, WARNING_MESSAGE, optionType, confirmAction, onConfirm, onCancel);
    }

    public static void makeCustomDialog(String title, Object message, int messageType,
                                        int optionType, int confirmAction,
                                        Runnable onConfirm, Runnable onCancel) {
        showDialog(title, message, messageType, optionType, confirmAction, onConfirm, onCancel);
    }

    public static void makeErrorAlert(String text) {
        JOptionPane.showMessageDialog(null, text, "Error", ERROR_MESSAGE);
    }

    public static void makeAlert(String title, String text, int messageType,
                                 int optionType, int confirmAction,
                                 Runnable onConfirm, Runnable onCancel) {
        final JComponent[] label = new JComponent[]{new JLabel(text)};
        showDialog(title, label, messageType, optionType, confirmAction, onConfirm, onCancel);
    }

    public static void showDialog(String title, Object message, int messageType,
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

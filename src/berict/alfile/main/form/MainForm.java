package berict.alfile.main.form;

import berict.alfile.Main;
import berict.alfile.file.*;
import lib.FileDrop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static berict.alfile.file.File.*;
import static berict.alfile.file.FileProcessor.writeToFile;
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
    private JPanel rightPanel;
    private JPanel statusPanel;
    private JLabel status;

    private ButtonGroup radioGroup;

    public static TableModel tableModel;

    public static int WINDOW_WIDTH = 960;
    public static int WINDOW_HEIGHT = 540;

    public static int WINDOW_MIN_WIDTH = 600;
    public static int WINDOW_MIN_HEIGHT = 420;

    TableModelRenderer renderer;

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

        ImageIcon img = new ImageIcon("resource/icon.png");
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
        initCenterLayout();
        initEastLayout();
        initSouthLayout();
        initWestLayout();
    }

    private void initCenterLayout() {
        tableModel = new TableModel();
        table.setModel(tableModel);
        //table.setSize(500, 300);
        table.setRowHeight(30);
        table.getColumn("Type").setMaxWidth(48);

        // add styles
        renderer = new TableModelRenderer(new Runnable() {
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
                        Main.log("start = " + start[0] + " / end = " + end[0]);
                    } else {
                        table.setRowSelectionInterval(end[0], start[0]);
                        Main.log("start = " + end[0] + " / end = " + start[0]);
                    }
                } else {
                    table.clearSelection();
                }

                int row[] = table.getSelectedRows();
                if (row.length <= 0) {
                    return;
                }

                onSelect(row, e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        tableModel.addTableModelListener(new TableModelListener());
    }

    private void initSouthLayout() {
        statusPanel = new JPanel();
        status = new JLabel("0 items", JLabel.LEFT);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(status, BorderLayout.WEST);
        statusPanel.setBorder(new EmptyBorder(2, 8, 2, 0));
        add("South", statusPanel);
    }

    private void initWestLayout() {
        radioGroup = new ButtonGroup();
        radioGroup.add(processAllButton);
        radioGroup.add(processSelectedButton);
        processAllButton.setSelected(true);

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
                    regexHelp.add(getRegexHelp(oldString), BorderLayout.WEST);

                    JLabel helpText = new JLabel("Click to append expression.");
                    helpText.setBorder(new EmptyBorder(4, 0, 8, 0));
                    regexHelp.add(helpText, BorderLayout.SOUTH);

                    JCheckBox useRegex = new JCheckBox("Use regular expression");
                    useRegex.setSelected(true);
                    useRegex.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            Main.log("useRegex = " + useRegex.isSelected());
                            regexHelp.setVisible(useRegex.isSelected());
                            regexHelp.revalidate();
                            all.setVisible(useRegex.isSelected());
                            all.revalidate();
                            first.setVisible(useRegex.isSelected());
                            first.revalidate();
                        }
                    });

                    useRegex.setAlignmentX(Component.LEFT_ALIGNMENT);
                    oldString.setAlignmentX(Component.LEFT_ALIGNMENT);
                    newString.setAlignmentX(Component.LEFT_ALIGNMENT);
                    all.setAlignmentX(Component.LEFT_ALIGNMENT);
                    first.setAlignmentX(Component.LEFT_ALIGNMENT);

                    Component inputs[] = new Component[]{
                            regexHelp,
                            new JLabel("String to replace"),
                            oldString,
                            new JLabel("New string"),
                            newString,
                            useRegex,
                            all,
                            first,
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
                                                if (!useRegex.isSelected()) {
                                                    // replace no regex
                                                    for (int row : table.getSelectedRows()) {
                                                        tableModel.get(row)
                                                                .getFile()
                                                                .replace(oldString.getText(), newString.getText());
                                                    }
                                                } else if (all.isSelected()) {
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
                                                                if (!useRegex.isSelected()) {
                                                                    // replace no regex
                                                                    for (int row = 0; row < table.getRowCount(); row++) {
                                                                        tableModel.get(row)
                                                                                .getFile()
                                                                                .replace(oldString.getText(), newString.getText());
                                                                    }
                                                                } else if (all.isSelected()) {
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
                                    Main.log(insertString.getText());
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
        JMenuItem reverse = new JMenuItem("Reverse file name"); // #32

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

        reverse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeWarningDialog("This will reverse the file name. Continue?", YES_NO_OPTION, YES_OPTION,
                        new Runnable() {
                            @Override
                            public void run() {
                                if (table.getSelectedRows().length > 0) {
                                    // has selected rows
                                    for (int row : table.getSelectedRows()) {
                                        String reversed = new StringBuilder(tableModel.get(row).getFile().getFileName(true))
                                                .reverse()
                                                .toString();
                                        tableModel.get(row).getFile().setName(reversed, false);
                                    }
                                    tableModel.update();
                                } else {
                                    // doesn't have selected rows
                                    makeWarningDialog("No file selected. Apply to all files?", YES_NO_OPTION, YES_OPTION,
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (int row = 0; row < table.getRowCount(); row++) {
                                                        String reversed = new StringBuilder(tableModel.get(row).getFile().getFileName(true))
                                                                .reverse()
                                                                .toString();
                                                        tableModel.get(row).getFile().setName(reversed, false);
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
        popup.add(reverse);

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
                                        int folderCount = 0;
                                        int moveCount = 0;
                                        int errorCount = 0;
                                        for (int row : table.getSelectedRows()) {
                                            printSubfolderResult(row, folderCount, moveCount, errorCount);
                                        }
                                        tableModel.update();
                                        makeAlert("Subfolder", "Processed "
                                                        + folderCount + " folder(s) with "
                                                        + moveCount + " move(s) and "
                                                        + errorCount + " error(s)", INFORMATION_MESSAGE, CLOSED_OPTION, CLOSED_OPTION,
                                                null, null);
                                    } else {
                                        // doesn't have selected rows
                                        makeWarningDialog("No folder selected. Apply to all folders?", YES_NO_OPTION, YES_OPTION,
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        int folderCount = 0;
                                                        int moveCount = 0;
                                                        int errorCount = 0;
                                                        for (int row = 0; row < table.getRowCount(); row++) {
                                                            printSubfolderResult(row, folderCount, moveCount, errorCount);
                                                        }
                                                        tableModel.update();
                                                        makeAlert("Subfolder", "Processed "
                                                                        + folderCount + " folder(s) with "
                                                                        + moveCount + " move(s) and "
                                                                        + errorCount + " error(s)", INFORMATION_MESSAGE, CLOSED_OPTION, CLOSED_OPTION,
                                                                null, null);
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
                if (table.getRowCount() > 0) {
                    // table has content

                    ButtonGroup buttonGroup = new ButtonGroup();
                    JRadioButton txt = new JRadioButton("Export to .txt");
                    JRadioButton csv = new JRadioButton("Export to .csv");
                    JRadioButton clipboard = new JRadioButton("Copy to clipboard");

                    buttonGroup.add(txt);
                    buttonGroup.add(csv);
                    buttonGroup.add(clipboard);

                    clipboard.setSelected(true);

                    final JComponent[] inputs = new JComponent[]{
                            new JLabel("Export format"),
                            txt,
                            csv,
                            clipboard,
                    };

                    makeDialog("Export content", inputs, OK_CANCEL_OPTION, OK_OPTION,
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (table.getSelectedRows().length > 0) {
                                        // has selected rows
                                        for (int row : table.getSelectedRows()) {
                                            File file = tableModel.get(row).getFile();
                                            String content = file.getDirectoryContent();
                                            if (clipboard.isSelected()) {
                                                // clipboard
                                                StringSelection stringSelection = new StringSelection(content);
                                                Clipboard clipboard1 = Toolkit.getDefaultToolkit().getSystemClipboard();
                                                clipboard1.setContents(stringSelection, null);
                                            } else if (txt.isSelected()) {
                                                // txt
                                                if (!writeToFile(file.getFullPath() + SEPARATOR + "content.alfile.txt", content)) {
                                                    makeErrorAlert("Error writing to file");
                                                }
                                            } else {
                                                // csv
                                                if (!writeToFile(file.getFullPath() + SEPARATOR + "content.alfile.csv", content)) {
                                                    makeErrorAlert("Error writing to file");
                                                }
                                            }
                                        }
                                        tableModel.update();
                                    } else {
                                        // doesn't have selected rows
                                        makeErrorAlert("No folder selected");
                                    }
                                }
                            }, null);

                } else {
                    makeErrorAlert("No file found");
                }
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
                            Main.log("process all");
                            for (int row = 0; row < table.getRowCount(); row++) {
                                process(row);
                                processCount++;
                            }
                        } else {
                            makeErrorAlert("Duplicated file name found");
                        }
                    } else if (processSelectedButton.isSelected()) {
                        if (!tableModel.hasDuplicate(table.getSelectedRow())) {
                            Main.log("process selected");
                            for (int row : table.getSelectedRows()) {
                                process(row);
                                processCount++;
                            }
                        } else {
                            makeErrorAlert("Duplicated file name found");
                        }
                    }
                    Main.log("Processed " + processCount);
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

    private String getNonRegex(String regex) {
        String regexSpecial[] = {"[", "\\", "^", "$", ".", "|", "?", "*", "+", "(", ")", "{", "}"};
        String result = regex;

        for (String special : regexSpecial) {
            result = result.replace(special, '\\' + special);
        }

        return result;
    }

    private void initEastLayout() {
        rightPanel.setMinimumSize(new Dimension(120, WINDOW_MIN_HEIGHT));
    }

    private void setPreviewStatus(String title, File file) {
        JPanel preview = new JPanel();
        preview.setBorder(new EmptyBorder(8, 8, 8, 8));
        preview.setLayout(new BoxLayout(preview, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(file.getFileName());
        titleLabel.setFont(new Font("Default", Font.PLAIN, 14));
        titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));

        preview.add(titleLabel);

        if (file != null) {
            JLabel type = new JLabel(file.getType());
            type.setFont(new Font("Default", Font.ITALIC, 13));
            type.setBorder(new EmptyBorder(0, 0, 8, 0));
            preview.add(type);

            if (file.isImage()) {
                ImageFile image = new ImageFile(file);
                JLabel imageLabel = new JLabel(image.getResizedImageIcon(200));
                imageLabel.setMinimumSize(new Dimension(200, 200));
                imageLabel.setMaximumSize(new Dimension(200, 200));
                imageLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
                preview.add(imageLabel);
            }

            JLabel size = new JLabel("Size : " + convertSize(file.length()));
            size.setFont(new Font("Default", Font.PLAIN, 12));
            preview.add(size);

            JLabel modified = new JLabel("Date modified : " + convertTime(file.getOriginal().lastModified()));
            modified.setFont(new Font("Default", Font.PLAIN, 12));
            preview.add(modified);
        }

        rightPanel.add(preview);
        rightPanel.revalidate();
    }

    public String convertSize(long bytes) {
        boolean si = true;
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return format.format(date);
    }

    private void printSubfolderResult(int row, int folderCount, int moveCount, int errorCount) {
        if (tableModel.get(row).getFile().isDirectory()) {
            folderCount++;
            // TODO make this as a customizable
            int result = tableModel.get(row).getFile().moveSubfolder("#");
            if (result > 0) {
                moveCount += result;
            } else {
                errorCount++;
            }
            setStatus("Processed "
                    + folderCount + " folder(s) with "
                    + moveCount + " move(s) and "
                    + errorCount + " error(s)");
        }
    }

    private void onSelect(int row[], MouseEvent e) {
        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            JPopupMenu popup = new JPopupMenu("Edit");
            JMenuItem undo = new JMenuItem("Undo");
            JMenuItem revert = new JMenuItem("Revert all changes");
            JMenuItem history = new JMenuItem("Show history");
            JMenuItem remove = new JMenuItem("Remove from list");
            JMenuItem process = new JMenuItem("Process");

            if (tableModel.isModified(row)) {
                undo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (int index : row) {
                            if (tableModel.get(index).isModified()) {
                                tableModel.get(index).getFile().undo();
                                Main.log("Menu.undo index=" + index);
                            }
                        }
                        tableModel.update();
                    }
                });
                revert.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (int index : row) {
                            if (tableModel.get(index).isModified()) {
                                tableModel.get(index).getFile().revert();
                                Main.log("Menu.revert index=" + index);
                            }
                        }
                        tableModel.update();
                    }
                });

                if (row.length > 1) {
                    history.setEnabled(false);
                } else {
                    final int[] selectedRow = {-1};
                    final FileTableItem item = tableModel.get(row[0]);
                    history.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String history[] = item.getFile().getHistory();
                            JTable historyTable = new JTable();

                            historyTable.setRowSelectionAllowed(true);
                            historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            historyTable.setRowHeight(24);

                            class HistoryTableModel extends AbstractTableModel {

                                public String[][] data = new String[0][1];
                                public String[][] tableData = new String[0][1];

                                public HistoryTableModel(String[] data) {
                                    this.data = new String[data.length][1];
                                    this.tableData = new String[data.length][1];
                                    for (int i = 0; i < data.length; i++) {
                                        // only display 24 characters
                                        this.data[i][0] = data[i];
                                        this.tableData[i][0] = "..." + data[i].substring(data[i].length() - 36);
                                    }
                                    Main.log(Arrays.deepToString(data));
                                }

                                @Override
                                public int getRowCount() {
                                    return data.length;
                                }

                                @Override
                                public int getColumnCount() {
                                    return 1;
                                }

                                @Override
                                public Object getValueAt(int rowIndex, int columnIndex) {
                                    return tableData[rowIndex][columnIndex];
                                }

                                @Override
                                public boolean isCellEditable(int rowIndex, int columnIndex) {
                                    return false;
                                }
                            }

                            class HistoryTableModelRenderer extends DefaultTableCellRenderer {

                                public HistoryTableModelRenderer() {
                                    setOpaque(true);
                                    setHorizontalAlignment(SwingConstants.LEFT);
                                }

                                @Override
                                public Component getTableCellRendererComponent(JTable table, Object value,
                                                                               boolean isSelected, boolean hasFocus,
                                                                               int row, int column) {
                                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                    if (row == historyTable.getSelectedRow()) {
                                        component.setBackground(hex2Rgb("#0078D7"));
                                        component.setFont(new Font("Default", Font.PLAIN, 11));
                                        setToolTipText(null);
                                    } else if (row > historyTable.getSelectedRow()) {
                                        component.setBackground(Color.LIGHT_GRAY);
                                        component.setFont(new Font("Default", Font.ITALIC, 11));
                                        setToolTipText("This will be undone");
                                    } else {
                                        component.setBackground(Color.WHITE);
                                        component.setFont(new Font("Default", Font.PLAIN, 11));
                                        setToolTipText(null);
                                    }
                                    component.setForeground(Color.BLACK);
                                    return component;
                                }

                                public Color hex2Rgb(String colorStr) {
                                    return new Color(
                                            Integer.valueOf(colorStr.substring(1, 3), 16),
                                            Integer.valueOf(colorStr.substring(3, 5), 16),
                                            Integer.valueOf(colorStr.substring(5, 7), 16));
                                }
                            }

                            HistoryTableModel historyTableModel = new HistoryTableModel(history);
                            HistoryTableModelRenderer modelRenderer = new HistoryTableModelRenderer();
                            TableColumnModel historyColumnModel = historyTable.getColumnModel();
                            for (int i = 0; i < historyColumnModel.getColumnCount(); i++) {
                                historyColumnModel.getColumn(i).setCellRenderer(modelRenderer);
                            }

                            historyTable.addMouseListener(new MouseListener() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    for (int i = 0; i < historyColumnModel.getColumnCount(); i++) {
                                        historyColumnModel.getColumn(i).setCellRenderer(modelRenderer);
                                    }
                                }

                                @Override
                                public void mousePressed(MouseEvent e) {
                                }

                                @Override
                                public void mouseReleased(MouseEvent e) {
                                    selectedRow[0] = historyTable.getSelectedRow();
                                    Main.log("Selected " + selectedRow[0]);
                                }

                                @Override
                                public void mouseEntered(MouseEvent e) {
                                }

                                @Override
                                public void mouseExited(MouseEvent e) {
                                }
                            });
                            historyTable.setModel(historyTableModel);
                            // select the last item
                            historyTable.setRowSelectionInterval(
                                    historyTable.getRowCount() - 1,
                                    historyTable.getRowCount() - 1
                            );
                            historyTable.setDefaultRenderer(Object.class, renderer);

                            JComponent components[] = {
                                    historyTable,
                                    new JLabel("Click item to undo")
                            };
                            showDialog("History", components,
                                    PLAIN_MESSAGE, OK_OPTION, OK_OPTION,
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Main.log("Close");
                                            if (selectedRow[0] >= 0) {
                                                Main.log("Undo to " + selectedRow[0]);
                                                tableModel.get(row[0]).getFile().undo(selectedRow[0]);
                                                tableModel.update();
                                            }
                                        }
                                    }, null);
                        }
                    });
                }

                process.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (int index : row) {
                            if (tableModel.get(index).isModified()) {
                                if (!tableModel.get(index).getFile().apply(tableModel)) {
                                    makeErrorAlert("Failed to process");
                                }
                                Main.log("Menu.process index=" + index);
                            }
                        }
                    }
                });
            } else {
                undo.setEnabled(false);
                history.setEnabled(false);
                process.setEnabled(false);
                revert.setEnabled(false);
            }

            remove.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = row.length - 1; i >= 0; i--) {
                        int index = row[i];
                        tableModel.remove(index);
                        Main.log("Menu.remove index=" + index);
                    }
                }
            });

            popup.add(undo);
            popup.add(revert);
            popup.add(history);
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

        setPreviewStatus("Test", tableModel.get(row[0]).getFile());
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
                        textTarget.setText(textTarget.getText() + expression.getText());
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
            Main.log(ex.getMessage());
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

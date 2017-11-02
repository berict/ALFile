package view;

import javax.swing.*;

public class AlFile extends JFrame {

    private JPanel MainPanel;
    private JPanel FilesPanel;
    private JPanel ProcessOptionPanel;
    private JPanel ProcessFunctionPanel;
    private JButton processButton;
    private JCheckBox selectedCheckBox;
    private JCheckBox allCheckBox;
    private JList functionList;
    private JList originalNameList;
    private JList fixedNameList;
    private JList fIleLocationList;
    private JLabel emptyFunctionLabel;

    public AlFile() {
        setSize(800, 600);
        setContentPane(MainPanel);
        setLocationRelativeTo(null);
    }

    public JButton getProcessButton() {
        return processButton;
    }

    public JCheckBox getSelectedCheckBox() {
        return selectedCheckBox;
    }

    public JCheckBox getAllCheckBox() {
        return allCheckBox;
    }

    public JList getFunctionList() {
        return functionList;
    }

    public JList getOriginalNameList() {
        return originalNameList;
    }

    public JList getFixedNameList() {
        return fixedNameList;
    }

    public JList getfIleLocationList() {
        return fIleLocationList;
    }

    public JLabel getEmptyFunctionLabel() {
        return emptyFunctionLabel;
    }
}

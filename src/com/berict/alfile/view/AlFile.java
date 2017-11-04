package com.berict.alfile.view;

import javax.swing.*;

public class AlFile extends JFrame {

    private JPanel mainPanel;
    private JPanel filesPanel;
    private JPanel processOptionPanel;
    private JPanel processFunctionPanel;
    private JButton processButton;
    private JCheckBox selectedCheckBox;
    private JCheckBox allCheckBox;
    private JList functionList;
    private JList originalNameList;
    private JList fixedNameList;
    private JList fIleLocationList;
    private JLabel emptyFunctionLabel;
    private JLabel originalName;
    private JLabel fixedName;
    private JLabel fileLocation;

    public AlFile() {
        setSize(1280, 720);
        setContentPane(mainPanel);
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

package com.berict.alfile.view;

import javax.swing.*;

public class MainForm extends JFrame {

    private JPanel rootPanel;

    private JPanel tablesPanel;
    private JLabel nameBeforeLabel;
    private JLabel nameAfterLabel;
    private JLabel locationLabel;
    private JList nameBeforeList;
    private JList nameAfterList;
    private JList locationList;

    private JPanel processOptionPanel;
    private JCheckBox selectedCheckBox;
    private JCheckBox allCheckBox;

    private JPanel processFunctionPanel;
    private JList functionList;

    private JButton processButton;

    public MainForm() {
        setSize(1280, 720);
        setTitle("AlFile");
        setContentPane(rootPanel);
        setLocationRelativeTo(null);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JPanel getTablesPanel() {
        return tablesPanel;
    }

    public JLabel getNameBeforeLabel() {
        return nameBeforeLabel;
    }

    public JLabel getNameAfterLabel() {
        return nameAfterLabel;
    }

    public JLabel getLocationLabel() {
        return locationLabel;
    }

    public JList getNameBeforeList() {
        return nameBeforeList;
    }

    public JList getNameAfterList() {
        return nameAfterList;
    }

    public JList getLocationList() {
        return locationList;
    }

    public JPanel getProcessOptionPanel() {
        return processOptionPanel;
    }

    public JCheckBox getSelectedCheckBox() {
        return selectedCheckBox;
    }

    public JCheckBox getAllCheckBox() {
        return allCheckBox;
    }

    public JPanel getProcessFunctionPanel() {
        return processFunctionPanel;
    }

    public JList getFunctionList() {
        return functionList;
    }

    public JButton getProcessButton() {
        return processButton;
    }
}

package com.berict.alfile.view;

import javax.swing.*;

public class MainForm extends JFrame {

    private JPanel rootPanel;

    private JPanel tablesPanel;
    private JLabel nameBeforeLable;
    private JLabel nameAfterLable;
    private JLabel locationLable;
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
        setContentPane(rootPanel);
        setLocationRelativeTo(null);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public JPanel getTablesPanel() {
        return tablesPanel;
    }

    public JLabel getNameBeforeLable() {
        return nameBeforeLable;
    }

    public JLabel getNameAfterLable() {
        return nameAfterLable;
    }

    public JLabel getLocationLable() {
        return locationLable;
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

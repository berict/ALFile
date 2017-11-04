package com.berict.alfile.controller;

import com.berict.alfile.view.AlFile;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlFileController {

    private AlFile alfile;
    private JButton processButton;
    private JCheckBox selectedCheckBox;
    private JCheckBox allCheckBox;
    private JList functionList;
    private JList originalNameList;
    private JList fixedNameList;
    private JList fIleLocationList;
    private JLabel emptyFunctionLabel;


    public AlFileController() {
        initComponents();
        initListeners();
    }

    public void showWindow() {
        alfile.setVisible(true);
    }

    private void initComponents() {
        alfile = new AlFile();

        processButton = alfile.getProcessButton();
        selectedCheckBox = alfile.getSelectedCheckBox();
        allCheckBox = alfile.getAllCheckBox();
        functionList = alfile.getFunctionList();
        originalNameList = alfile.getOriginalNameList();
        fixedNameList = alfile.getFixedNameList();
        fIleLocationList = alfile.getfIleLocationList();
        emptyFunctionLabel = alfile.getEmptyFunctionLabel();
    }

    private void initListeners() {
        processButton.addActionListener(new processButtonListener());
        functionList.addListSelectionListener(new functionListListener());
    }

    private class processButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){

        }
    }

    private class functionListListener implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {

        }
    }
}

package com.berict.alfile.controller;

import com.berict.alfile.view.MainForm;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainController {

    private MainForm mainForm;

    public MainController() {
        initComponents();
        initListeners();
    }

    public void setVisible(boolean visible) {
        mainForm.setVisible(visible);
    }

    private void initComponents() {
        mainForm = new MainForm();
    }

    private void initListeners() {
        mainForm.getProcessButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                println("actionPerformed()");
            }
        });

        mainForm.getFunctionList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                println("valueChanged()");
            }
        });
    }

    private static void println(Object object) {
        // this is a logging method
        System.out.println(object.toString());
    }
}

package controller;

import view.AlFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlFileController {

    private AlFile alfile;
    private JButton btn1;
    private JCheckBox selectedCheckBox;
    private JCheckBox allCheckBox;
    private JList list;


    public AlFileController() {
        initComponents();
        initListeners();
    }

    public void showWindow() {
        alfile.setVisible(true);
    }

    private void initComponents() {
        alfile = new AlFile();

        btn1 = alfile.getBtn1();
        selectedCheckBox = alfile.getSelectedCheckBox();
        allCheckBox = alfile.getSelectedCheckBox();
        list = alfile.getList();
    }

    private void initListeners() {
        btn1.addActionListener(new btn1Listener());
    }

    private class btn1Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){

        }
    }
}

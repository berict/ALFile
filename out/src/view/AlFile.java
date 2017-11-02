package view;

import javax.swing.*;

public class AlFile extends JFrame {

    private JPanel Panel;
    private JButton btn1;
    private JCheckBox selectedCheckBox;
    private JCheckBox allCheckBox;
    private JList list;

    public AlFile() {
        setSize(800, 600);
        setContentPane(Panel);
        setLocationRelativeTo(null);
    }

    public JButton getBtn1() {
        return btn1;
    }

    public JCheckBox getSelectedCheckBox() {
        return selectedCheckBox;
    }

    public JCheckBox getAllCheckBox() {
        return allCheckBox;
    }

    public JList getList() {
        return list;
    }
}

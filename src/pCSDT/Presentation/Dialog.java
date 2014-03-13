/*
 * PClass.java
 *
 * Created on October 30, 2007, 3:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package pCSDT.Presentation;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Dialog extends JDialog implements ActionListener {

    public GridLayout layout;
    public JLabel nameLabel;
    public JTextField nameField;
    public JButton OKButton;
    public JButton CancelButton;

    /** Creates a new instance of PClass */
    public Dialog() {
        super((JFrame) null, "Creating New Object");
        setSize(300, 100);
        createDialog();
        setVisible(true);
    }

    public void createDialog() {
        layout = new GridLayout(0, 2);
        getContentPane().setLayout(layout);

        nameLabel = new JLabel("Name");
        nameField = new JTextField();

        add(nameLabel, nameField);

        OKButton = new JButton("OK");
        CancelButton = new JButton("Cancel");
        CancelButton.addActionListener(this);
        
        add (OKButton, CancelButton);
    }

    public void add(JComponent a, JComponent b) {
        getContentPane().add(a);
        getContentPane().add(b);
    }
    public void showDialog(){
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            setVisible(false);
    }
}

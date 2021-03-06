/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDlgCreateObj.java
 *
 * Created on Aug 11, 2010, 6:05:36 PM
 */

package pCSDT.Presentation;

import java.lang.annotation.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import pCSDT.Scripting.*;
import javax.swing.DefaultListModel;

/**
 *
 * @author tylau
 */
public class JDlgCreateObj extends javax.swing.JDialog {

    PEngine pEngine;  // handle to pEngine
    JPnlObjMgr jPnlObjMgr;  // handle to JPnlObjectManager
    DefaultListModel model = new DefaultListModel();
    GUI gui;  // handle to GUI

    /** Creates new form JDlgCreateObj */
    public JDlgCreateObj(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setJDlgCreateObj(JPnlObjMgr jPnlObjMgr, GUI gui) {
        this.jPnlObjMgr = jPnlObjMgr;
        this.gui = gui;
        this.pEngine = this.jPnlObjMgr.pEngine;
        Class[] classes = this.pEngine.GetObjectTypes();
        for (Class cls: classes) {
            model.addElement(cls);
        }
        jListPObjCls.setModel(model);
        jListPObjCls.setSelectedIndex(jListPObjCls.getFirstVisibleIndex());
       
        ListItemRenderer renderer = new ListItemRenderer();
        jListPObjCls.setCellRenderer(renderer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListPObjCls = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaObjDesc = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Type");
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        jPanelButtons.setPreferredSize(new java.awt.Dimension(180, 35));

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonOK);

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonCancel);

        getContentPane().add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jListPObjCls.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListPObjCls.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListPObjClsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListPObjCls);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTextAreaObjDesc.setColumns(20);
        jTextAreaObjDesc.setEditable(false);
        jTextAreaObjDesc.setRows(5);
        jScrollPane3.setViewportView(jTextAreaObjDesc);

        jPanel2.add(jScrollPane3, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        Class newSel;
        int idx = jListPObjCls.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Please select a type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            newSel = (Class)model.get(idx);
            PObject obj = gui.ConstructObject(newSel, "", "");
            obj.SetTempName();
            gui.SetSelection(obj);
        }
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jListPObjClsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListPObjClsValueChanged
        int idx = jListPObjCls.getSelectedIndex();
        if (idx > -1) {
            Class selectedCls = (Class)model.get(idx);

            Annotation[] annotations = selectedCls.getAnnotations();
            boolean bFound = false;
            for (Annotation annotation: annotations) {
                AutomatableClass myAnnotation = (AutomatableClass)annotation;
                jTextAreaObjDesc.setText(myAnnotation.desc());
                bFound = true;
            }
            if (!bFound) {
                jTextAreaObjDesc.setText("");
            }

        }
        else {
            jTextAreaObjDesc.setText("");
        }
    }//GEN-LAST:event_jListPObjClsValueChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDlgCreateObj dialog = new JDlgCreateObj(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JList jListPObjCls;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextAreaObjDesc;
    // End of variables declaration//GEN-END:variables

    private class ListItemRenderer extends JLabel implements ListCellRenderer {
        public ListItemRenderer() {
            setOpaque(true);
        }
        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                                           JList list,
                                           Object value,
                                           int index,
                                           boolean isSelected,
                                           boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)

            //int selectedIndex = ((Integer)value).intValue();
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            /*
            // get the handle to the PObject being selected
            if (index > -1) {
                PObject selectedPObj = (PObject)model.get(index);
                jTextAreaObjDesc.setText(selectedPObj.GetClassDesc());
            }
            else {
                jTextAreaObjDesc.setText("");
            }
             */
            
            Class selectedCls = (Class)model.get(index);

            String pObjStr = selectedCls.getSimpleName();
            setText(pObjStr);

            /*
            //Set the icon and text.  If icon was null, say so.
            ImageIcon icon = images[selectedIndex];
            String pet = petStrings[selectedIndex];
            setIcon(icon);
            if (icon != null) {
                setText(pet);
                setFont(list.getFont());
            } else {
                setUhOhText(pet + " (no image available)",
                            list.getFont());
            }
            */

            return this;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPnlPopup.java
 *
 * Created on Jun 13, 2009, 5:43:40 AM
 */

package pCSDT.Presentation;

import pCSDT.Scripting.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

/**
 *
 * @author Jason
 */
public class JPnlPopup extends javax.swing.JPanel {
    DefaultTableModel mdl = new DefaultTableModel();

    /** Creates new form JPnlPopup */
    public JPnlPopup() {
        initComponents();

        mdl.addColumn("");
        mdl.addRow(new Object[]{"One"});
        mdl.addRow(new Object[]{"Two"});

        jTblMain.setModel(mdl);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTblMain = new javax.swing.JTable();

        jTblMain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTblMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTblMain;
    // End of variables declaration//GEN-END:variables

}
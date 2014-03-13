/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPnlPropMgr.java
 *
 * Created on Mar 5, 2011, 7:50:52 AM
 */

package pCSDT.Presentation;

import java.awt.Color;
import java.awt.Component;
import pCSDT.Scripting.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.StringTokenizer;

/**
 *
 * @author tylau
 */
public class JPnlInitPropMgr extends JPnlMgr {

    static final Color WARNINGCOLOR = new Color(255, 255, 150);
    static final Color NORMALCOLOR = new Color(224, 223, 227);
    static PObject pObject = null;  // the current PObject
    CoordJDialog coordJDialog;

    String errMsg;  // current err msg
    String descMsg;  // current desc msg
    GUI gui;

    private class FormattedRenderer extends DefaultTableCellRenderer {

        JPnlInitPropMgr pMgr;

        public FormattedRenderer(JPnlInitPropMgr pMgr) {
            this.pMgr = pMgr;
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Component cell = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column );

            // validation
            if (column == 1) {
                String propName = table.getModel().getValueAt(row, 0).toString();
                int idx = -1;
                for (int j = 0; idx==-1 && j < curProps.length; j++) {
                    if (curProps[j].GetSimpleName().equals(propName)) {
                        idx = j;
                    }
                }
                if (idx != -1) {
                    errMsg = pObject.IsInputValid(value.toString(), propName);
                    if (errMsg!=null) {
                        cell.setBackground(WARNINGCOLOR);
                        if (table.getValueAt(table.getSelectedRow(), 0).equals(propName)) {
                            // change the jTxtDesc to err message
                            pMgr.jTextPane1.setText(errMsg);
                            pMgr.jTextPane1.setBackground(WARNINGCOLOR);
                        }
                    }
                    else {
                        cell.setBackground(Color.WHITE);
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow > -1) {
                            if (table.getValueAt(selectedRow, 0).equals(propName)) {
                                // change the jTxtDesc to normal msg
                                pMgr.jTextPane1.setText(descMsg);
                                pMgr.jTextPane1.setBackground(NORMALCOLOR);
                            }
                        }
                    }
                }
            }
            else {
                cell.setBackground(Color.WHITE);
            }

            return cell;
        }
    }

    DefaultTableModel mdl = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 0 || col == 2) {
                return false;
            }
            return true;
        }
    };
    DefaultTableModel mdlAdv = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 0 || col == 2) {
                return false;
            }
            return true;
        }
    };

    ListSelectionModel lSelMdl, lSelMdlAdv;
    static PProperty[] curProps = null;
    PProperty selProp;
    JPnlObjMgr objMgr; // handle to JPnlObjectManager to get it up-to-date

    /** Creates new form JPnlPropMgr */
    public JPnlInitPropMgr() {
        initComponents();
    }

    /** Creates new form JPnlInitPropertyMgr */
    public JPnlInitPropMgr(JPnlObjMgr aObjMgr) {
        initComponents();
        coordJDialog = new CoordJDialog(null, true);
        objMgr = aObjMgr;

        // set up for the basic property window
        try {
            jTblBasic.setDefaultRenderer(Class.forName("java.lang.Object"), new FormattedRenderer(this));
        }
        catch (Exception e) {}

        jTblBasic.addMouseListener(new MouseAdapter(){
             @Override
    	     public void mousePressed(MouseEvent e){
    	         TableModel Tmodel=jTblBasic.getModel();
                 int rowno=jTblBasic.getSelectedRow();
    	    	 String coloumnName=(String)Tmodel.getValueAt(rowno,0);
                 ColorPopUp(coloumnName, e, Tmodel, rowno);
                 ChooseGraphicPopUp(coloumnName, e, Tmodel, rowno);
                 ChooseSoundPopUp(coloumnName, e, Tmodel, rowno);
                 CoordinatesPopUp(coloumnName, e, Tmodel, rowno);
             }
    	} );
        mdl.addColumn("Name");
        mdl.addColumn("Value");
        mdl.addColumn("Display name");
        mdl.addTableModelListener(
            new TableModelListener()
            {
                boolean bRecurse = false;

                @Override
                public void tableChanged(TableModelEvent e)
                {
                    if (pObject != null) {
                        pObject.Reset();
                    }

                    if(bRecurse)
                        return;

                    // Ignore nonspecific:
                    if(e.getType() != TableModelEvent.UPDATE)
                        return;

                    // now updating all properties of the object
                    // Acquire and attempt to coerce from string form...
                    //for (int i = 0; i < curProps.length; i++) {
                    for (int i = 0; i < mdl.getRowCount(); i++) {
                        Object val = mdl.getValueAt(i, e.getColumn());
                        String propName = mdl.getValueAt(i, 2).toString();
                        
                        // get index to the required curProps
                        int idx = -1;
                        for (int j = 0; idx==-1 && j < curProps.length; j++) {
                            if (curProps[j].GetSimpleName().equals(propName)) {
                                idx = j;
                            }
                        }
                        if (idx == -1) {
                            return;
                        }

                        PPropertyUnbound ub = curProps[idx].GetUnboundProperty();
                        PVariant coerced;
                        coerced = PVariant.FromString(val.toString(), ub.GetType());
                        if(coerced != null)
                            try
                            {
                                curProps[idx].SetValue(coerced);
                                //return;
                            }
                            catch(Exception x)
                            {
                                x.printStackTrace();
                            }

                        // Veto the change (set the recursion flag first)
                        bRecurse = true;
                        mdl.setValueAt(curProps[idx].GetValue(), i, e.getColumn());
                        bRecurse = false;
                    }
                    // update the view of the jPnlObjectManager
                    objMgr.updateUI();
                    objMgr.m_parent.UpdateUI();
                    objMgr.m_parent.repaint();
                }
            }
        );


        jTblBasic.setModel(mdl);
        ////jTblBasic.getColumnModel().getColumn(2).setMaxWidth(0);
        jTblBasic.removeColumn(jTblBasic.getColumnModel().getColumn(2));
        lSelMdl = jTblBasic.getSelectionModel();

        lSelMdl.addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (pObject != null) {
                        pObject.Reset();
                    }

                    int i = lSelMdl.getMinSelectionIndex();
                    if(i == -1 || curProps.length <= i || mdl.getRowCount() <= 0)
                        return;

                    String propName = mdl.getValueAt(i, 2).toString();
                    
                    // get index to the required curProps
                    int idx = -1;
                    for (int j = 0; idx==-1 && j < curProps.length; j++) {
                        if (curProps[j].GetSimpleName().equals(propName)) {
                            idx = j;
                        }
                    }
                    if (idx == -1) {
                        return;
                    }

                    PPropertyUnbound ub = curProps[idx].GetUnboundProperty();
                    descMsg = ub.GetDesc();
                    jTextPane1.setText(descMsg);

                    // update the view of the jPnlObjectManager
                    objMgr.updateUI();
                    objMgr.m_parent.UpdateUI();
                    objMgr.m_parent.repaint();
                }
            }
        );







        // set up for the basic property window
        try {
            jTblAdv.setDefaultRenderer(Class.forName("java.lang.Object"), new FormattedRenderer(this));
        }
        catch (Exception e) {}

        jTblAdv.addMouseListener(new MouseAdapter(){
             @Override
    	     public void mousePressed(MouseEvent e){
    	         TableModel Tmodel=jTblAdv.getModel();
                 int rowno=jTblAdv.getSelectedRow();
    	    	 String coloumnName=(String)Tmodel.getValueAt(rowno,0);
                 ColorPopUp(coloumnName, e, Tmodel, rowno);
                 ChooseGraphicPopUp(coloumnName, e, Tmodel, rowno);
                 CoordinatesPopUp(coloumnName, e, Tmodel, rowno);
             }
    	} );
        mdlAdv.addColumn("Name");
        mdlAdv.addColumn("Value");
        mdlAdv.addColumn("Display name");
        mdlAdv.addTableModelListener(
            new TableModelListener()
            {
                boolean bRecurse = false;

                @Override
                public void tableChanged(TableModelEvent e)
                {
                    if (pObject != null) {
                        pObject.Reset();
                    }

                    if(bRecurse)
                        return;

                    // Ignore nonspecific:
                    if(e.getType() != TableModelEvent.UPDATE)
                        return;

                    // now updating all properties of the object
                    // Acquire and attempt to coerce from string form...
                    //for (int i = 0; i < curProps.length; i++) {
                    for (int i = 0; i < mdlAdv.getRowCount(); i++) {
                        Object val = mdlAdv.getValueAt(i, e.getColumn());
                        String propName = mdlAdv.getValueAt(i, 2).toString();
                        // get index to the required curProps
                        int idx = -1;
                        for (int j = 0; idx==-1 && j < curProps.length; j++) {
                            if (curProps[j].GetSimpleName().equals(propName)) {
                                idx = j;
                            }
                        }
                        if (idx == -1) {
                            return;
                        }

                        PPropertyUnbound ub = curProps[idx].GetUnboundProperty();
                        PVariant coerced;
                        coerced = PVariant.FromString(val.toString(), ub.GetType());
                        if(coerced != null)
                            try
                            {
                                curProps[idx].SetValue(coerced);
                                //return;
                            }
                            catch(Exception x)
                            {
                                x.printStackTrace();
                            }

                        // Veto the change (set the recursion flag first)
                        bRecurse = true;
                        mdlAdv.setValueAt(curProps[idx].GetValue(), i, e.getColumn());
                        bRecurse = false;
                    }
                    // update the view of the jPnlObjectManager
                    objMgr.updateUI();
                    objMgr.m_parent.UpdateUI();
                    objMgr.m_parent.repaint();
                }
            }
        );

        jTblAdv.setModel(mdlAdv);
        jTblAdv.removeColumn(jTblAdv.getColumnModel().getColumn(2));
        lSelMdlAdv = jTblAdv.getSelectionModel();

        lSelMdlAdv.addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (pObject != null) {
                        pObject.Reset();
                    }

                    int i = lSelMdlAdv.getMinSelectionIndex();
                    if(i == -1 || curProps.length <= i || mdlAdv.getRowCount() <= 0)
                        return;

                    String propName = mdlAdv.getValueAt(i, 2).toString();
                    
                    // get index to the required curProps
                    int idx = -1;
                    for (int j = 0; idx==-1 && j < curProps.length; j++) {
                        if (curProps[j].GetSimpleName().equals(propName)) {
                            idx = j;
                        }
                    }
                    if (idx == -1) {
                        return;
                    }

                    PPropertyUnbound ub = curProps[idx].GetUnboundProperty();
                    descMsg = ub.GetDesc();
                    jTextPane1.setText(descMsg);
                    
                    // update the view of the jPnlObjectManager
                    objMgr.updateUI();
                    objMgr.m_parent.UpdateUI();
                    objMgr.m_parent.repaint();
                }
            }
        );

    }
    public void ChooseGraphicPopUp(String columnName, MouseEvent e, TableModel Tmodel, int rowno) {

        int ifColor1=columnName.indexOf("Icon");
        int ifColor2=columnName.indexOf("icon");

        if(ifColor1!=-1 || ifColor2!=-1)
        {
            JFileChooser chooser = new JFileChooser();
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                public boolean accept(File pathname) {
                    // allow directory to be seen even with filtering
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    return getExtension(pathname).compareTo("png") == 0;
                }

                public String getDescription() {
                    return ".png";
                }

                public String getExtension(File f)
                {
                    String name = f.getName();
                    int i = name.lastIndexOf(".");
                    if(i < 0)
                        return "";
                    return name.substring(i + 1).toLowerCase();
                }
            };
            chooser.setFileFilter(filter);
           int returnVal = chooser.showOpenDialog(this.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
              
               

                 Tmodel.setValueAt(chooser.getSelectedFile().getAbsolutePath(), rowno, 1);
            }
                
            
                   
        }
    }
    public void ChooseSoundPopUp(String columnName, MouseEvent e, TableModel Tmodel, int rowno) {

        int ifColor1=columnName.indexOf("Sound");
        int ifColor2=columnName.indexOf("sound");

        if(ifColor1!=-1 || ifColor2!=-1)
        {
            JFileChooser chooser = new JFileChooser();
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                public boolean accept(File pathname) {
                    // allow directory to be seen even with filtering
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    return getExtension(pathname).compareTo("mp3") == 0;
                }

                public String getDescription() {
                    return ".mp3";
                }

                public String getExtension(File f)
                {
                    String name = f.getName();
                    int i = name.lastIndexOf(".");
                    if(i < 0)
                        return "";
                    return name.substring(i + 1).toLowerCase();
                }
            };
            chooser.setFileFilter(filter);
           int returnVal = chooser.showOpenDialog(this.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
              
               

                 Tmodel.setValueAt(chooser.getSelectedFile().getAbsolutePath(), rowno, 1);
            }
                
            
                   
        }
    }


    public void ColorPopUp(String columnName, MouseEvent e, TableModel Tmodel, int rowno) {

        if(!GUI.reducedColorSelection){
        int ifColor1=columnName.indexOf("Color");
        int ifColor2=columnName.indexOf("color");

        if(ifColor1!=-1 || ifColor2!=-1)
        {

            Color br = JColorChooser.showDialog(
    	         ((Component)e.getSource()).getParent(),
    	         "Pick the color", Color.blue);
            if(br != null)
            {
                 String colorStr=br.getRed()+","+br.getGreen()+","+br.getBlue();

                 Tmodel.setValueAt(colorStr, rowno, 1);
            }
        }
        } else {

            int ifReducedColor1=columnName.indexOf("Color1");

            if(ifReducedColor1!=-1)
            {

                 String[] choices = {"Black", "Dark Gray", "Light Gray"};

                 int response = JOptionPane.showOptionDialog(
                               null                       // Center in window.
                             , ""        // Message
                             , "Pick a color"               // Title in titlebar
                             , JOptionPane.YES_NO_OPTION  // Option type
                             , JOptionPane.PLAIN_MESSAGE  // messageType
                             , null                       // Icon (none)
                             , choices                    // Button text as above.
                             , ""    // Default button's label
                           );

            //... Use a switch statement to check which button was clicked.
            switch (response) {
                case 0:
                    String colorStr = "0,0,0" ;
                    Tmodel.setValueAt(colorStr, rowno, 1);
                    break;
                case 1:
                    colorStr = "169,169,169" ;
                    Tmodel.setValueAt(colorStr, rowno, 1);;
                    break;
                case 2:
                     colorStr = "200,200,200" ;
                    Tmodel.setValueAt(colorStr, rowno, 1);;
                    break;

                

                
                 
            }
        }
        }}

    public void CoordinatesPopUp(String columnName, MouseEvent e, TableModel Tmodel, int rowno) {
        int ifCoord1=columnName.indexOf("(Coord)");
        int ifCoord2=columnName.indexOf("(coord)");

        if(ifCoord1!=-1 || ifCoord2!=-1)
        {
            this.coordJDialog = new CoordJDialog(null, true);
            StringTokenizer tokens = new StringTokenizer(Tmodel.getValueAt(rowno, 1).toString(), ",");
            if (tokens.hasMoreTokens()) {
                coordJDialog.SetTextFieldX(tokens.nextToken().trim());
            }
            if (tokens.hasMoreTokens()) {
                coordJDialog.SetTextFieldY(tokens.nextToken().trim());
            }
            coordJDialog.EnablingOKButton();
            coordJDialog.setVisible(true);

            if(this.coordJDialog.coordStr != null)
            {
                 Tmodel.setValueAt(this.coordJDialog.coordStr, rowno, 1);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPaneBasic = new javax.swing.JScrollPane();
        jTblBasic = new javax.swing.JTable();
        jScrollPaneAdv = new javax.swing.JScrollPane();
        jTblAdv = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setOneTouchExpandable(true);

        jPanel2.setMinimumSize(new java.awt.Dimension(64, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(457, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(64, 0));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(457, 0));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jScrollPaneBasic.setPreferredSize(new java.awt.Dimension(452, 100));

        jTblBasic.setModel(new javax.swing.table.DefaultTableModel(
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
        jTblBasic.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneBasic.setViewportView(jTblBasic);

        jTabbedPane1.addTab("Starting Values", jScrollPaneBasic);

        jScrollPaneAdv.setPreferredSize(new java.awt.Dimension(452, 100));

        jTblAdv.setModel(new javax.swing.table.DefaultTableModel(
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
        jTblAdv.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneAdv.setViewportView(jTblAdv);

        jTabbedPane1.addTab("Advanced", jScrollPaneAdv);

        jPanel2.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setTopComponent(jPanel2);

        jPanel1.setMinimumSize(new java.awt.Dimension(23, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(166, 30));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jTextPane1.setContentType("text/html");
        jTextPane1.setEditable(false);
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jScrollPane1.setViewportView(jTextPane1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setBottomComponent(jPanel1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        jTblBasic.clearSelection();
        jTblAdv.clearSelection();
        jTextPane1.setText("");
    }//GEN-LAST:event_jTabbedPane1StateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneAdv;
    private javax.swing.JScrollPane jScrollPaneBasic;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTblAdv;
    private javax.swing.JTable jTblBasic;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

    ///
    // Base class overrides:
    ///

    @Override
    public void BindObject(PObject obj)
    {
        try
        {
            // Clear the rows first...
            mdl.setRowCount(0);
            mdlAdv.setRowCount(0);
            if(obj == null)
            {
                curProps = null;
                pObject = null;
                return;
            }

            pObject = obj;
            curProps = obj.GetProperties();
            if(curProps.length == 0)
                return;

            // Reset row count:
            Object[] row = new Object[3];
            for(int i = 0; i < curProps.length; i++)
            {
                PProperty prop = curProps[i];
                PPropertyUnbound ub = prop.GetUnboundProperty();

                row[2] = ub.GetName();
                row[1] = prop.GetValue();
                row[0] = ub.GetDisplayName();
                String p = ub.GetDesignTimeProperties();
                if (p.equals("B")) {
                    mdl.addRow(row);
                }
                else if (p.equals("A")) {
                    mdlAdv.addRow(row);
                }
            }
            jTblBasic.clearSelection();
            jTblAdv.clearSelection();
            jTextPane1.setText("");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }
}

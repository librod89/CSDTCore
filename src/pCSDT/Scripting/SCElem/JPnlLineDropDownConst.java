/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.*;
import pCSDT.Scripting.*;

/**
 *
 * @author Jason
 */
public class JPnlLineDropDownConst extends JPnlLineDraggable {
    JComboBox jComboBox;
    boolean m_palletObj = false;

    class JComboBoxListener implements ActionListener, MouseMotionListener {
        JPnlLineDropDownConst line;
        boolean bDragged = false;  // whether the textfield has been dragged anyway

        public JComboBoxListener(JPnlLineDropDownConst line) {
            this.line = line;
        }

        public void mouseDragged(MouseEvent e) {
            if (!bDragged) {
                return;
            }
            if (p != null) {
                bDragged = true;
                MouseEvent eMod = SwingUtilities.convertMouseEvent(line, e, p);
                p.dispatchEvent(eMod);
            }
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            PStatementDynamicConst s = GetStatement();
            s.SetSelectedIndex(cb.getSelectedIndex());
        }

    }
  
    public JPnlLineDropDownConst(PStatementDynamicConst s, LayoutInfo info)
    {
        super(s, info);
    }

    @Override
    public void InitControls()
    {
        
        PStatementDynamicConst s = GetStatement();
        jComboBox = new JComboBox(s.GetValueList().toArray());
        if (s.GetValueList().size() > 0) {
            jComboBox.setSelectedIndex(s.GetSelectedIndex());
        }
        JComboBoxListener listener = new JComboBoxListener(this);
        jComboBox.setPreferredSize(new Dimension(jComboBox.getPreferredSize().width,18));
        jComboBox.addMouseMotionListener(listener);
        jComboBox.addActionListener(listener);
        add(jComboBox);
        SetGridDimensions(1,1);
       
    }

    @Override
    public eSaveResult Save()
    {
        GetStatement().SetSelectedIndex(jComboBox.getSelectedIndex());
        return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b)
    {
        jComboBox.setEditable(b);
        jComboBox.setFocusable(b);
	jComboBox.setEnabled(b);
    }

    @Override
    public PStatementDynamicConst GetStatement() {return (PStatementDynamicConst)m_statement;}

    @Override
    public JPnlLineDropDownConst clone()
    {
	return new JPnlLineDropDownConst(GetStatement().clone(), m_l);
    }

}

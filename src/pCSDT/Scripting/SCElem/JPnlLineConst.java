/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import pCSDT.Presentation.JPnlDragParticipant;
import pCSDT.Scripting.*;
import pCSDT.Utility;

/**
 *
 * @author Jason
 */
public class JPnlLineConst extends JPnlLineDraggable {
    JTextField jTxtConst;
    boolean m_palletObj = false;

    class JTxtConstListener implements MouseListener, MouseMotionListener, KeyListener {
        JPnlLineConst line;
        boolean bDragged = false;  // whether the textfield has been dragged anyway

        public JTxtConstListener(JPnlLineConst line) {
            this.line = line;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (!jTxtConst.isFocusOwner()) {
                p = Utility.GetTypedAncestor(JPnlDragParticipant.class, line);
                if (p != null) {
                    MouseEvent eMod = SwingUtilities.convertMouseEvent(line, e, p);
                    p.dispatchEvent(eMod);
                }
            }
            bDragged = false;
        }

        public void mouseReleased(MouseEvent e) {
            if (p != null) {
                MouseEvent eMod = SwingUtilities.convertMouseEvent(line, e, p);
                p.dispatchEvent(eMod);
            }
            p = null;
            bDragged = false;
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            if (jTxtConst.isFocusOwner() && !bDragged) {
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

        public void keyPressed(KeyEvent e) {}

        public void keyReleased(KeyEvent e)
        {
            String txt = line.jTxtConst.getText().trim();
            GetStatement().SetValue(PVariant.FromString(txt));
        }

        public void keyTyped(KeyEvent e) {}
    }

    public JPnlLineConst(LayoutInfo info)
    {
	this(new PStatementConst("Literal"), info);
	m_palletObj = true;
    }

    public JPnlLineConst(PStatementConst s, LayoutInfo info)
    {
        super(s, info);
    }

    @Override
    public void InitControls()
    {
        jTxtConst = new JTextField(GetStatement().GetValue().toString()) {
            // avoid border of JTextField
            @Override public void setBorder(Border border) {}
        };
        jTxtConst.setMinimumSize(new Dimension(10,18));
        ////jTxtConst.setPreferredSize(new Dimension(16,18));
        JTxtConstListener listener = new JTxtConstListener(this);
        jTxtConst.addKeyListener(listener);
        jTxtConst.addMouseListener(listener);
        jTxtConst.addMouseMotionListener(listener);
        jTxtConst.setHorizontalAlignment(JTextField.CENTER);

        add(jTxtConst);

        SetGridDimensions(1, 1);
    }

    @Override
    public eSaveResult Save()
    {
        PVariant var = PVariant.FromString(jTxtConst.getText());
        GetStatement().SetValue(var);
        return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b)
    {
        jTxtConst.setEditable(b);
        jTxtConst.setFocusable(b);
	jTxtConst.setEnabled(b);
    }

    @Override
    public PStatementConst GetStatement() {return (PStatementConst)m_statement;}

    @Override
    public JPnlLineConst clone()
    {
	// If this is a pallet object, we do not bother cloning the statement:
	if(m_palletObj)
            return new JPnlLineConst(new PStatementConst(""), m_l);
	return new JPnlLineConst(GetStatement().clone(), m_l);
    }

}

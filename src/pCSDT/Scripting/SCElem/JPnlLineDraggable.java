/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import pCSDT.Presentation.JPnlDragParticipant;
import pCSDT.Scripting.*;
import pCSDT.Utility;
import resource.layouts.SpringUtilities;

/**
 * This is a single line of codelet
 * @author Jason
 */
public abstract class JPnlLineDraggable extends JPnlLine implements MouseListener, MouseMotionListener
{
    public JPnlLineDraggable(IStatement statement, LayoutInfo l)
    {
        super(statement, l);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public JPnlLineDraggable(IStatement statement, LayoutInfo l, LayoutManager layout)
    {
        super(statement, l, layout);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        p = Utility.GetTypedAncestor(JPnlDragParticipant.class, this);
        if (p != null) {
            MouseEvent eMod = SwingUtilities.convertMouseEvent(this, e, p);
            p.dispatchEvent(eMod);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (p != null) {
            MouseEvent eMod = SwingUtilities.convertMouseEvent(this, e, p);
            p.dispatchEvent(eMod);
        }
        p = null;
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
        if (p != null) {
            MouseEvent eMod = SwingUtilities.convertMouseEvent(this, e, p);
            p.dispatchEvent(eMod);
        }
    }

    public void mouseMoved(MouseEvent e) {}
}

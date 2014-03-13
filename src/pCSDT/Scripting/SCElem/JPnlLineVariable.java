/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
import javax.swing.border.Border;
import pCSDT.Presentation.JPnlDragParticipant;
import pCSDT.Scripting.*;
import pCSDT.Utility;

/**
 *
 *
 * @author Jason Sanchez
 */
public class JPnlLineVariable extends JPnlLineDraggable {

    static Image img;

    JTextField f;

    class FListener implements MouseListener, MouseMotionListener, KeyListener {
        JPnlLineVariable line;
        boolean bDragged = false;  // whether the textfield has been dragged anyway

        public FListener(JPnlLineVariable line) {
            this.line = line;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (!f.isFocusOwner()) {
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
            if (line.isEnabled()) {
                if (f.isFocusOwner() && !bDragged) {
                    return;
                }
                if (p != null) {
                    bDragged = true;
                    MouseEvent eMod = SwingUtilities.convertMouseEvent(line, e, p);
                    p.dispatchEvent(eMod);
                }
            }
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void keyPressed(KeyEvent e) {}

        public void keyReleased(KeyEvent e)
        {
            String txt = f.getText().trim();
            GetStatement().SetName(txt);
        }

        public void keyTyped(KeyEvent e) {}
    }

    public JPnlLineVariable(PStatementVariable s, LayoutInfo info)
    {
	super(s, info);
        setOpaque(false);
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletProperty.png");
        }
    }

    /*
	public INamedAssignable GetStatement() {
        return (INamedAssignable)super.GetStatement();
    }
     */
    @Override
    public PStatementVariable GetStatement() {
        return (PStatementVariable)super.GetStatement();
    }

    @Override
    public void InitControls()
    {
        f = new JTextField() {
            // avoid border of JTextField
            @Override public void setBorder(Border border) {}
        };
        f.setText(GetStatement().GetName());
        f.setPreferredSize(new Dimension(24, 14));
        f.setMinimumSize(new Dimension(24, 14));

        FListener listener = new FListener(this);
        f.addKeyListener(listener);
        f.addMouseListener(listener);
        f.addMouseMotionListener(listener);

        f.setHorizontalAlignment(JTextField.CENTER);
        JPanel middlePanel = new JPanel();
        middlePanel.setMinimumSize(new Dimension(30, 20));
        middlePanel.setPreferredSize(new Dimension(30, 20));
        middlePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,3,3,3);
        middlePanel.add(f, c);
        middlePanel.setOpaque(false);
        add(middlePanel);
        SetGridDimensions(1,1);
        // Default the size to the preferred size, with border allowance
        // Default the size to the preferred size:
        setMinimumSize(getPreferredSize());
        ////Dimension ps = getPreferredSize();
        ////setMinimumSize(new Dimension(ps.width+2*BORDERWIDTH,ps.height+2*BORDERWIDTH));
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, getWidth(), getHeight(), 0, 0, 349, 81, this);
        super.paintComponent(g);
    }

    @Override
    public eSaveResult Save()
    {
	String name = f.getText();
	if(name.indexOf(".") == -1)
            GetStatement().SetName(f.getText());
        return eSaveResult.Success;
    }

    @Override
    protected boolean ChildRemoveAllowed(JPnlLine child) {return false;}

    @Override
    public void Highlight(InsertionPosition highlight) {}

    @Override
    public void ClearHighlight() {}

    @Override
    public void setEditable(boolean b)
    {
        f.setEnabled(b);
        f.setEditable(b);
    }

    @Override
    public JPnlLineVariable clone()
    {
	return new JPnlLineVariable(
                    GetStatement().clone(),
                    m_l
		);
    }
}

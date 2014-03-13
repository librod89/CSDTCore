/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.*;
import pCSDT.Scripting.*;

/**
 * This is a line inserted for a property definition on an object
 * @author Jason Sanchez
 */
public class JPnlLineReadOnlyProperty extends JPnlLineDraggable
{
    static Image img;

    static Color COLOR_ENABLE = new Color(250, 76, 254);
    static Color COLOR_DISABLE = new Color(255, 190, 255);

    JLabel m_propLabel;

    public JPnlLineReadOnlyProperty(PStatementReadOnlyProperty s, LayoutInfo info)
    {
	super(s, info);
        setOpaque(false);
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletReadOnlyProperty.png");
        }
    }

    @Override
    public void InitControls()
    {
	PStatementReadOnlyProperty s = GetStatement();
	PProperty prop = s.GetProperty();

        if (prop != null) {
            m_propLabel = new JLabel(prop.GetUnboundProperty().GetName());
            m_propLabel.setHorizontalAlignment(JLabel.CENTER);
            m_propLabel.setVerticalAlignment(JLabel.CENTER);
            JPanel middlePanel = new JPanel();
            middlePanel.setMinimumSize(new Dimension(10, 13));
            middlePanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3,3,3,3);
            middlePanel.add(m_propLabel, c);
            middlePanel.setOpaque(false);
            add(middlePanel);
            SetGridDimensions(1,1);
            // Default the size to the preferred size:
            setMinimumSize(getPreferredSize());
            ////Dimension ps = getPreferredSize();
            ////setMinimumSize(new Dimension(ps.width+2*BORDERWIDTH, ps.height+2*BORDERWIDTH));
        }
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
	return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b)
    {
	////m_propbox.setEnabled(false);
    }

    @Override
    public PStatementReadOnlyProperty GetStatement() {
        return (PStatementReadOnlyProperty)super.GetStatement();
    }

    @Override
    public JPnlLineReadOnlyProperty clone()
    {
	return new JPnlLineReadOnlyProperty(GetStatement(), m_l);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import pCSDT.Scripting.*;

/**
 * This is a null event line used for orphaned statements that are still children of a single
 * object
 *
 * @author Jason Sanchez
 */
public class JPnlLineEventNull extends JPnlLineEvent
{
    public JPnlLineEventNull(PEventNull nEvt, LayoutInfo l)
    {
	super(nEvt, l);
        setMinimumSize(new Dimension(50,16));


        setLayout(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
    }

    @Override
    public void InitControls()
    {
        // This event has no name, and so does not require a label.
	// Construct the statement list that follows the label:
	IStatementList l = m_evt.GetListener();
	int iNComponents = 0;
	if(!m_bIsBlank && l != null && l.GetChildren().length != 0)
	{
            m_lLineList = (JPnlLineList)l.GetGui(m_l);
            m_lLineList.InitControls();
            add(m_lLineList, c);
            c.gridy++;
            iNComponents++;
	}

	// Default the size to the preferred size:
	setSize(getPreferredSize());
    }

    @Override
    public eSaveResult Save()
    {
	return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b)
    {
    }

    @Override
    public void DrawComponent(Graphics g)
    {
    }

    @Override
    public JPnlLineEventNull clone()
    {
	return new JPnlLineEventNull((PEventNull)m_evt, m_l);
    }
}

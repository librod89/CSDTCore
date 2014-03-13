/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import pCSDT.Scripting.*;
import pCSDT.Scripting.PStatementList;

/**
 *
 * @author Jason Sanchez
 */
public abstract class JPnlLineControl extends JPnlLineDraggable
{
    static Image img;
    
    public JPnlLineControl(PStatementControl s, LayoutInfo info)
    {
        super(s, info);
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletControl.png");
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, getWidth(), 5, 0, 0, 360, 10, this);
        g.drawImage(img, 0, 5, getWidth(), getHeight()-5, 0, 11, 360, 70, this);
        g.drawImage(img, 0, getHeight()-5, getWidth(), getHeight(), 0, 71, 360, 80, this);
        super.paintComponent(g);
    }

    public abstract JPnlLineControl clone();
    public abstract JPnlLine GetCondition();
    public abstract Container GetConditionContainer();
    public abstract int GetConditionOffsetY();
    public abstract JPnlLine[] GetBodies();
    public abstract Container[] GetBodyContainers();

    @Override
    public PStatementControl GetStatement() {return (PStatementControl)m_statement;}

    @Override
    public boolean Insert(InsertionPosition p)
    {
	if(!(p instanceof InsertionPositionControl))
            return false;

	InsertionPositionControl pos = (InsertionPositionControl)p;
	PStatementControl c = GetStatement();
        IStatement stmt = null;  // stmt to be displaced
	switch(pos.m_rel)
	{
            case BodyFirst:
		c.GetBodies()[pos.m_iBody].InsertChild(pos.GetStatement(), 0);
		break;
            case BodyLast:
		c.GetBodies()[pos.m_iBody].AppendChild(pos.GetStatement());
		break;
            case Conditional:
                // create a null event for the original statements if any
                stmt = c.GetCondition();
                if (stmt != null && !(stmt instanceof PStatementConst || stmt instanceof PStatementNull)) {
                    if (stmt instanceof PStatementList) {
                        PStatementList pstmtlist = (PStatementList)stmt;
                        if (!pstmtlist.IsEmpty()) {
                            CreateNullEvent(stmt, 0, GetConditionOffsetY());
                        }
                    }
                    else {
                        CreateNullEvent(stmt, 0, GetConditionOffsetY());
                    }
                }
		c.SetCondition(pos.GetStatement());
		break;
            case Miss:
		// Ignore a miss.
            break;
	}
	return true;
    }

    @Override
    public void DrawInsertionPosition(Graphics2D g)
    {
	InsertionPositionControl highlight = (InsertionPositionControl)m_highlight;
	switch(highlight.m_rel)
	{
            case BodyFirst:
            case BodyLast:
		FrameComponent(GetBodyContainers()[highlight.m_iBody], g);
		break;
            case Conditional:
		FrameComponent(GetConditionContainer(), g);
		break;
            default:
		return;
	}
    }

    @Override
    protected void NotifyChildRemoved(JPnlLine child)
    {
	PStatementControl c = GetStatement();
	if(child == GetCondition())
            c.SetCondition(new PStatementList());

	JPnlLine[] bodies = GetBodies();
	for(int i = 0; i < bodies.length; i++)
            if(bodies[i] == child) {
		c.SetBody(i, new PStatementList());
		break;
            }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import pCSDT.Scripting.*;
import java.util.ArrayList;
import pCSDT.Utility;

/**
 * This is a central class for all statements.  It's the only one designed to
 * represent more than one unrelated statement, though other statements can potentially
 * represent more than one related statement (for instance, the binary operator requires
 * two substatements, both of which may be represented).  This class is used implicitly
 * by While and If, as both the PStatementWhile and PStatementIf statements use statement
 * lists internally to represent the sequence executable statements.
 *
 * In any case, the line list operates by using a layout elements to create a new line for
 * each line of text read.
 * @author Jason
 */
public class JPnlLineList extends JPnlLine
{
    ArrayList<JPnlLine> subLines = new ArrayList<JPnlLine>();

    public JPnlLineList(PStatementList s, LayoutInfo info)
    {
        super(s, info);
    }

    @Override
    public void InitControls()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,0,-5,0);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        
        // Create a layout:
        IStatement[] children = m_statement.GetChildren();
        if(children.length == 0)
            return;

        for(int i=0; i < children.length; i++)
        {
            JPnlLine cur = children[i].GetGui(m_l);
            subLines.add(cur);
            cur.InitControls();
            if (i == children.length-1) {
                c.insets = new Insets(0,0,0,0);
            }
            add(cur, c);
            c.gridy++;
        }
        
        setMinimumSize(getPreferredSize());
    }

	@Override
	public void ReinitControls()
	{
		// Clear the sublines and pass control
		subLines.clear();
		super.ReinitControls();
	}

    @Override
    public eSaveResult Save()
    {
        for(JPnlLine cur : subLines)
        {
            eSaveResult rs = cur.Save();
            if(rs != eSaveResult.Success)
                return rs;
        }
        return eSaveResult.Success;
    }

    @Override
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        validate();  // make sure the layout is updated

	InsertionPosition[] possibles = new InsertionPosition[subLines.size()];
	if(!s.HasSideEffect())
	{
            for(int i = 0; i < subLines.size(); i++)
            {
                JPnlLine l = subLines.get(i);
		possibles[i] = l.GetInsertionPosition(Utility.Transform(this, pt, l), s);
            }
            return InsertionPosition.GetBestPosition(possibles);
	}

	for(int i = 0; i < subLines.size(); i++)
	{
            JPnlLine l = subLines.get(i);
            Point adj = Utility.Transform(this, pt, l);

            possibles[i] = InsertionPosition.GetBestPosition(
            l.GetInsertionPosition(adj, s),
			new InsertionPositionList(this, s, Math.abs(adj.y), i)
            );
	}

	return InsertionPosition.GetBestPosition(
			InsertionPosition.GetBestPosition(possibles),
			new InsertionPositionList(this, s, Math.abs(getHeight() - pt.y), subLines.size())
               );
    }

    @Override
    public void DrawInsertionPosition(Graphics2D g)
    {
        if(m_highlight == null)
            return;

        InsertionPositionList highlight = (InsertionPositionList)m_highlight;
	if(subLines.isEmpty())
            DrawHorizontalHighlight(g, 0);
	else if(highlight.m_index == subLines.size())
	{
            JPnlLine l = subLines.get(subLines.size() - 1);
            DrawHorizontalHighlight(g, l.getY() + l.getHeight()-5);
	}
	else
            DrawHorizontalHighlight(g, subLines.get(highlight.m_index).getY());
    }

	@Override
	public boolean Insert(InsertionPosition insert)
	{
		if(insert instanceof InsertionPositionList)
		{
			InsertionPositionList l = (InsertionPositionList)insert;
			PStatementList lst = (PStatementList)m_statement;

			if(l.m_index == lst.GetChildren().length)
				lst.AppendChild(l.m_s);
			else
				lst.InsertChild(
					l.m_s,
					l.m_index
				);
			return true;
		}
		return false;
	}

	@Override
	public void Highlight(InsertionPosition highlight)
	{
		super.Highlight(highlight);
	}

	@Override
	public void ClearHighlight()
	{
		for(JPnlLine l : subLines) {
            l.ClearHighlight();
        }
        super.ClearHighlight();
	}

	@Override
	public PStatementList GetStatement() {return (PStatementList)super.GetStatement();}

	@Override
	protected void NotifyChildRemoved(JPnlLine child)
	{
		int i = subLines.indexOf(child);
		if(i == -1)
			return;

		// Remove the child from the statement list and reinitialize:
		GetStatement().RemoveChild(i);
		ReinitControls();
	}

	@Override
	public void setEditable(boolean b)
	{
		for(JPnlLine cur : subLines)
			cur.setEditable(b);
	}

	@Override
	public JPnlLineList clone()
	{
		return new JPnlLineList(GetStatement().clone(), m_l);
	}
}

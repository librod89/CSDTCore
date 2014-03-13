/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.*;
import javax.swing.*;

import java.awt.*;
import pCSDT.Utility;

/**
 *
 * @author Jason
 */
public class JPnlLineWhile extends JPnlLineControl
{
    // Panel for the conditional and the body:
    JPnlPlaceholderExpr m_condition;
    JPnlPlaceholderStmt m_body;

    JPnlLine m_lCondition;
    JPnlLine m_lBody;

    public JPnlLineWhile(LayoutInfo info)
    {
        this(
            new PStatementWhile(
                new PStatementList(),
                new PStatementList()
            ),
            info
	);
    }

    public JPnlLineWhile(PStatementWhile s, LayoutInfo info)
    {
	super(s, info);
	setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    public PStatementWhile GetStatement() {return (PStatementWhile)m_statement;}

    @Override
    public JPnlLine GetCondition() {return m_lCondition;}

    @Override
    public Container GetConditionContainer() {return m_condition;}

    @Override
    public int GetConditionOffsetY() {
        Point ploc = getLocationOnScreen();
        Point loc = m_condition.getLocationOnScreen();
        return loc.y - ploc.y;
    }
    
    @Override
    public JPnlLine[] GetBodies() {return new JPnlLine[]{m_lBody};}

    @Override
    public Container[] GetBodyContainers() {return new Container[]{m_body};}

    @Override
    public void InitControls()
    {
        SetDefaultBorder("", null);
        m_lCondition = GetStatement().GetCondition().GetGui(m_l);
        m_lCondition.InitControls();

        m_condition = new JPnlPlaceholderExpr();
        m_condition.setBackground(new java.awt.Color(157,129,35));
        m_condition.add(m_lCondition);

    //Set up the header for this codelet
		JPanel pnl = new JPanel();
		pnl.setLayout(new SpringLayout());
		pnl.add(new JLabel("Repeat while"));
		pnl.add(m_condition);
		pnl.setOpaque(false);
		SetGridDimensions(pnl, 2, 1, 4, 2, 4, 2);
		add(pnl);
	
	//GetStatement grabs a PStatementWhile associated with this JPnlLine
	//GetBodies grabs the PStatementList associated with the inside of the PStatementWhile
	//GetGui returns the JPnlLineList associated with that PStatementList
	//JPnlLineList extends JPnlLine, so m_lBody is one JPnlLine representing all of the JPnlLines in the body of this JPnlWhile
		m_lBody = GetStatement().GetBodies()[0].GetGui(m_l);
		m_lBody.InitControls();

        m_body = new JPnlPlaceholderStmt();
        m_body.setBackground(new java.awt.Color(187,159,65));
        m_body.add(m_lBody);
        add(m_body);

     // Add a border at the bottom
    	JPanel bottomborder = new JPanel();
    	bottomborder.setLayout(new SpringLayout());
    	bottomborder.add(new JLabel(""));
        bottomborder.setOpaque(false);
        SetGridDimensions(bottomborder, 1, 1, 4, 2, 4, 2);
        add(bottomborder);
        
    // Set everything into a grid in this codelet
        SetGridDimensions(1, 3, 3);

        // Default the size to the preferred size, with border allowance
        Dimension s = getPreferredSize();
        setMinimumSize(new Dimension(s.width+2*BORDERWIDTH,s.height+4*BORDERWIDTH));
    }

    @Override
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        validate();  // make sure the layout is updated

        Point adj;

	// Is the point within the bounds of the conditional?
	JPnlLine lConditional = GetCondition();
	adj = Utility.Transform(this, pt, lConditional);
	if(s.HasReturnValue() && lConditional.contains(adj))
	{
            // Does the conditional itself want the expression?
            InsertionPosition pos = lConditional.GetInsertionPosition(adj, s);
            if(pos != null)
                return pos;

            // The subcontrol must return Boolean, indicating an attempt is being
            // made to put a control block in the conditional expression's spot.
            if(s.GetReturnType() != PType.Boolean)
                return null;

            // Subcontrol doesn't want the position.
            return new InsertionPositionControl(
                                this,
				s,
				Math.min(
					adj.y,
					lConditional.getHeight() - adj.y
				),
				GetStatement(), s.GetReturnType().IsBoolean() ? InsertionPositionControl.RelTo.Conditional : InsertionPositionControl.RelTo.Miss
			);
	}

	for(JPnlLine body : GetBodies())
	{
            if(body == null)
		continue;

            // Spatial transform and content verification:
            adj = Utility.Transform(this, pt, body);
            if(body.contains(adj)) {
                // The body contains the point.  This means that we need to descend the body
                if (m_lBody != null) {
                    InsertionPosition pos = m_lBody.GetInsertionPosition(adj, s);
                    if (pos != null) {
                        return pos;
                    }
                }
                if (s.HasSideEffect()) {
                    return InsertionPosition.GetBestPosition(
                            	body.GetInsertionPosition(adj, s),
                                new InsertionPositionControl(
                                    this,
                                    s,
                                    Math.min(
                                        adj.y,
                                        getHeight() - adj.y
                                    ),
                                    GetStatement(), adj.y - 0 < getHeight() - adj.y ? InsertionPositionControl.RelTo.BodyFirst : InsertionPositionControl.RelTo.BodyLast
                                )
                            );
                }
            }
        }

	// Give up.
	return null;
    }

    @Override
    public void ClearHighlight()
    {
        if (m_lCondition != null)
            m_lCondition.ClearHighlight();
        if (m_lBody != null)
            m_lBody.ClearHighlight();
        super.ClearHighlight();
    }

    @Override
    public eSaveResult Save()
    {
	m_lCondition.Save();
	m_lBody.Save();
	return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b)
    {
	m_lCondition.setEditable(b);
	m_lBody.setEditable(b);
    }

    @Override
    public JPnlLineWhile clone()
    {
	return new JPnlLineWhile(GetStatement().clone(), m_l);
    }
}

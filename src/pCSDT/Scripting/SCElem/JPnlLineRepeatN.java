/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import pCSDT.Scripting.IStatement;
import pCSDT.Scripting.PStatementRepeatN;
import pCSDT.Utility;

/**
 * This is the GUI representation of PStatementRepeatN
 * @author Jason Sanchez
 */
public class JPnlLineRepeatN extends JPnlLineControl
{
    JPnlPlaceholderExpr m_condition;
    JPnlPlaceholderStmt m_body;

    JPnlLine m_lCondition;
    JPnlLine m_lBody;

    public JPnlLineRepeatN(LayoutInfo l)
    {
	this(new PStatementRepeatN(), l);
    }

    public JPnlLineRepeatN(PStatementRepeatN s, LayoutInfo l)
    {
	super(s, l);
    }

    @Override
    public void InitControls()
    {   
        SetDefaultBorder("", null);
        m_lCondition = GetStatement().GetCondition().GetGui(m_l);
        m_lCondition.InitControls();

        m_condition = new JPnlPlaceholderExpr();
        ////m_condition.setBackground(new java.awt.Color(187,159,65));
        ////m_condition.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
        m_condition.add(m_lCondition);

	//Create the header for this JPnlLine 
		JPanel pnl = new JPanel();
		pnl.setLayout(new SpringLayout());
		pnl.add(new JLabel("Repeat"));
		pnl.add(m_condition);
	    pnl.add(new JLabel("times"));
	    pnl.setOpaque(false);
	    SetGridDimensions(pnl, 3, 1, 4, 2, 4, 2);
	    add(pnl);

	//GetStatement grabs a PStatementRepeatNTimes associated with this JPnlLine
	//GetBodies grabs the PStatementList associated with the inside of the PStatementRNT
	//GetGui returns the JPnlLineList associated with that PStatementList
	//JPnlLineList extends JPnlLine, so m_lBody is one JPnlLine representing all of the JPnlLines in the body of this JPnlRepeatNTimes
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
	
	// Set grid for the whole thing
	    SetGridDimensions(1, 3, 3);

        // Default the size to the preferred size, with border allowance
        Dimension s = getPreferredSize();
        setMinimumSize(new Dimension(s.width+2*BORDERWIDTH,s.height+4*BORDERWIDTH));
        
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
    public PStatementRepeatN GetStatement() {return (PStatementRepeatN)m_statement;}

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
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        validate();  // make sure the layout is updated

        Point adj;

	// Is the point within the bounds of the conditional?
	adj = Utility.Transform(this, pt, m_condition);
        
        //// IN PROGRESS: would like to allow binary stmt to be inserted
        //// and default value if the constant is dragged away.
	if(s.HasReturnValue() && m_condition.contains(adj))
	{
            if (m_lCondition != null) {
                // Does the conditional itself want the expression?
                InsertionPosition pos = m_lCondition.GetInsertionPosition(adj, s);
                if(pos != null)
                    return pos;
            }

            // The subcontrol must not have a NULL type, indicating an attempt is being
            // made to put a control block in the conditional expression's spot.
            if(!s.HasReturnValue())
                return null;

            // Subcontrol doesn't want the position.
            return new InsertionPositionControl(
				this,
				s,
				Math.min(
					adj.y,
					m_lCondition.getHeight() - adj.y
				),
				GetStatement(), !(s.GetReturnType().IsBoolean()||s.GetReturnType().IsString()||s.GetReturnType().IsVoid())? InsertionPositionControl.RelTo.Conditional : InsertionPositionControl.RelTo.Miss
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

    public void SetConditionEnabled(boolean b) {
        m_lCondition.setEnabled(b);
    }

    @Override
    protected boolean ChildRemoveAllowed(JPnlLine child)
    {
	////return child != jPnlLhs;
        // disallow drag of default JPnlLineConst
        if (child == m_lCondition && m_lCondition instanceof JPnlLineConst) {
            return false;
        }
        return true;
    }

    @Override
    protected void NotifyChildRemoved(JPnlLine child) {
        ////super.NotifyChildRemoved(child);
        PStatementRepeatN s = GetStatement();
        if (m_lCondition == child) {
            // instead of setting null, set to default arg const value
            s.SetCondition(defaultConstStmt);
        }
    }

    @Override
    public JPnlLineRepeatN clone() {return new JPnlLineRepeatN(GetStatement().clone(), m_l);}
}

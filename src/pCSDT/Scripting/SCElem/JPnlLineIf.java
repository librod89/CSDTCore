/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import pCSDT.Scripting.*;
import javax.swing.*;

import pCSDT.Utility;

/**
 *
 * @author Jason
 */
public class JPnlLineIf extends JPnlLineControl
{
    JPnlPlaceholderExpr m_condition;
    JPnlPlaceholderStmt m_bodyTrue;
    JPnlPlaceholderStmt m_bodyFalse;
	
    JPnlLine m_lCondition;
    JPnlLine m_lBodyTrue;
    JPnlLine m_lBodyFalse;

    JPanel pnl;

    public JPnlLineIf(LayoutInfo info)
    {
	this(new PStatementIf(), info);
    }

    public JPnlLineIf(PStatementIf s, LayoutInfo l)
    {
        super(s, l);
    }

    @Override
    public PStatementIf GetStatement() {return (PStatementIf)m_statement;}

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
    public JPnlLine[] GetBodies() {return new JPnlLine[]{m_lBodyTrue, m_lBodyFalse};}

    @Override
    public Container[] GetBodyContainers() {return new Container[]{m_bodyTrue, m_bodyFalse};}

    @Override
    public void InitControls()
    {
        SetDefaultBorder("", null);
        m_lCondition = GetStatement().GetCondition().GetGui(m_l);
        m_lCondition.InitControls();

        m_condition = new JPnlPlaceholderExpr();
        m_condition.setBackground(new java.awt.Color(157,129,35));
        m_condition.add(m_lCondition);

    // Set up the header for this codelet
		pnl = new JPanel();
		pnl.add(new JLabel("If"));
		pnl.add(m_condition);
		pnl.setLayout(new SpringLayout());
		pnl.setOpaque(false);
		SetGridDimensions(pnl, 2, 1, 4, 2, 4, 2);
        add(pnl);

    //GetStatement grabs a PStatementIf associated with this JPnlLine
    //GetBodies grabs the PStatementList associated with the inside of the PStatementIf
    //GetGui returns the JPnlLineList associated with that PStatementList
    //JPnlLineList extends JPnlLine, so m_lBody is one JPnlLine representing all of the JPnlLines in the body of this JPnlIf   
		IStatementList[] bodies = GetStatement().GetBodies();
		m_lBodyTrue = bodies[0].GetGui(m_l);
		m_lBodyTrue.InitControls();
	
		m_bodyTrue = new JPnlPlaceholderStmt();
        m_bodyTrue.add(m_lBodyTrue);
        add(new JLabel("Do:"));
		add(m_bodyTrue);

        if(GetStatement().HasElseBranch()) {
        	
            m_lBodyFalse = bodies[1].GetGui(m_l);
            m_lBodyFalse.InitControls();

            add(new JLabel("Otherwise:"));

            m_bodyFalse = new JPnlPlaceholderStmt();
            m_bodyFalse.setBackground(new java.awt.Color(187,159,65));
            m_bodyFalse.add(m_lBodyFalse);
            add(m_bodyFalse);
        	
        // Add a border at the bottom
    		JPanel bottomborder = new JPanel();
    		bottomborder.setLayout(new SpringLayout());
    		bottomborder.add(new JLabel(""));
    	    bottomborder.setOpaque(false);
    	    SetGridDimensions(bottomborder, 1, 1, 4, 2, 4, 2);
    	    add(bottomborder);
            
    	// Set everything in this codelet into a grid
            SetGridDimensions(1, 6, 3);
            // Default the size to the preferred size, with border allowance
            Dimension s = getPreferredSize();
            setMinimumSize(new Dimension(s.width+2*BORDERWIDTH,s.height+6*BORDERWIDTH));
        }
        
        else {
        
        // Add a border at the bottom
    		JPanel bottomborder = new JPanel();
    		bottomborder.setLayout(new SpringLayout());
    		bottomborder.add(new JLabel(""));
    	    bottomborder.setOpaque(false);
    	    SetGridDimensions(bottomborder, 1, 1, 4, 2, 4, 2);
    	    add(bottomborder);
    	    
    	// Set everything in this codelet into a grid
        	SetGridDimensions(1, 4, 3);
            // Default the size to the preferred size, with border allowance
            Dimension s = getPreferredSize();
            setMinimumSize(new Dimension(s.width+2*BORDERWIDTH,s.height+4*BORDERWIDTH));
        }
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

            // The subcontrol must be a Boolean, indicating an attempt is being
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
                InsertionPosition posTrue = null, posFalse = null;
                if (m_lBodyTrue != null) {
                    Point adjTrue = Utility.Transform(this, pt, m_lBodyTrue);
                    posTrue = m_lBodyTrue.GetInsertionPosition(adjTrue, s);
                }
                if (m_lBodyFalse != null) {
                    Point adjFalse = Utility.Transform(this, pt, m_lBodyFalse);
                    posFalse = m_lBodyFalse.GetInsertionPosition(adjFalse, s);
                }
                if (posTrue != null && posFalse != null) {
                    if (posTrue.m_distance < posFalse.m_distance) return posTrue;
                    else return posFalse;
                }
                else if (posTrue == null) return posFalse;
                else if (posFalse == null) return posTrue;
                
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
    public void ClearHighlight()
	{
        if (m_lCondition != null)
            m_lCondition.ClearHighlight();
        if (m_lBodyTrue != null)
            m_lBodyTrue.ClearHighlight();
        if (m_lBodyFalse != null)
            m_lBodyFalse.ClearHighlight();
        super.ClearHighlight();
	}

    @Override
    public eSaveResult Save()
    {
	m_lCondition.Save();
	m_lBodyTrue.Save();
	if(m_lBodyFalse != null)
            m_lBodyFalse.Save();
	return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b)
    {
    }

    @Override
    public JPnlLineIf clone()
    {
	return new JPnlLineIf(GetStatement().clone(), m_l);
    }
}

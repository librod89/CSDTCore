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
 * @author Jason Sanchez
 */
public class JPnlLineAssign extends JPnlLineDraggable
{
    ////static Color COLOR_DISABLE = new Color(180, 6, 184);
    static Color COLOR_HOLE = new Color(255, 113, 253);

    JPnlPlaceholderExpr m_lhs = new JPnlPlaceholderExpr();
    JPnlPlaceholderExpr m_rhs = new JPnlPlaceholderExpr();

    JPnlLine jPnlLhs;
    JPnlLine jPnlRhs;
    Image img;
    
    public JPnlLineAssign(PStatementAssign s, LayoutInfo info)
    {
        super(s, info);
        img = GetImage("/resource/layouts/imgs/codeletExpression.png");
        setOpaque(false);
    }

    @Override
    public PStatementAssign GetStatement()
    {
        return (PStatementAssign)super.GetStatement();
    }

    @Override
    public void InitControls()
    {
        PStatementAssign s = GetStatement();
        SetDefaultBorder("", null);
        m_lhs.setBackground(COLOR_HOLE);
        IStatement lhsStmt = s.GetLhs();
        if (lhsStmt != null) {
            jPnlLhs = lhsStmt.GetGui(m_l);
            jPnlLhs.InitControls();
            m_lhs.add(jPnlLhs);
            if (lhsStmt instanceof PStatementDynamicConst) {
                m_lhs.setEnabled(false);
            }
        }
        else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BorderLayout());
            JPanel middlePanel = new JPanel();
            middlePanel.setMinimumSize(new java.awt.Dimension(30,12));
            middlePanel.setPreferredSize(new java.awt.Dimension(30,12));
            middlePanel.setOpaque(false);
            emptyPanel.add(middlePanel, BorderLayout.CENTER);
            emptyPanel.setOpaque(false);
            m_lhs.add(emptyPanel);
        }
        JLabel eq = new JLabel("=");
        eq.setHorizontalAlignment(SwingConstants.CENTER);
        IStatement rhsStmt = s.GetRhs();
        if (rhsStmt != null) {
            jPnlRhs = rhsStmt.GetGui(m_l);
            jPnlRhs.InitControls();
            m_rhs.add(jPnlRhs);
            if (rhsStmt instanceof PStatementDynamicConst) {
                m_rhs.setEnabled(false);
            }
        }
        JPanel eqPanel = new JPanel();
        eqPanel.setOpaque(false);
        eqPanel.setPreferredSize(new java.awt.Dimension(10,12));
        eqPanel.setLayout(new BorderLayout());
        
        add(m_lhs);
        eqPanel.add(eq);
        add(eqPanel, BorderLayout.CENTER);
        add(m_rhs);
        SetGridDimensions(3, 1, 4);

        // Default the size to the preferred size, with border allowance
        setMinimumSize(getPreferredSize());
    }

    @Override
    protected boolean ChildRemoveAllowed(JPnlLine child)
    {
	////return child != jPnlLhs;
        // disallow drag of default JPnlLineConst
        if (child == jPnlRhs && jPnlRhs instanceof JPnlLineConst) {
            return false;
        }
        return true;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, getWidth(), 5, 0, 0, 360, 10, this);
        g.drawImage(img, 0, 5, getWidth(), getHeight()-5, 0, 11, 360, 70, this);
        g.drawImage(img, 0, getHeight()-5, getWidth(), getHeight(), 0, 71, 360, 80, this);
       super.paintComponent(g);
    }

    @Override
    public eSaveResult Save()
    {
        eSaveResult rs;

        // Save the variable name:
        rs = jPnlLhs.Save();
        if(rs != eSaveResult.Success)
            return rs;
       
        // Save the RHS value.
        rs = jPnlRhs.Save();
        if(rs != eSaveResult.Success)
            return rs;

		// Success!
        return eSaveResult.Success;
    }

    @Override
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        validate();  // make sure the layout is updated

	Point adj;

	// Expression must have an evaluation:
	if(!s.HasReturnValue())
            return null;

	adj = Utility.Transform(this, pt, m_lhs);
	if(m_lhs.isEnabled() && m_lhs.contains(adj)) {
            // Expression must be of type PStatementProperty
            if (!(s instanceof PStatementProperty || s instanceof PStatementVariable)) {
                return null;
            }
            // see if inner stuff wants it to be inserted
            if (jPnlLhs != null) {
                InsertionPosition pos = jPnlLhs.GetInsertionPosition(adj, s);
                if (pos != null)
                    return pos;
            }
            return new InsertionPositionAssign(this, s, 0, true);
        }
            
	adj = Utility.Transform(this, pt, m_rhs);
	if(m_rhs.isEnabled() && m_rhs.contains(adj)) {
            // see if inner stuff wants it to be inserted
            if (jPnlRhs != null) {
                InsertionPosition pos = jPnlRhs.GetInsertionPosition(adj, s);
                if (pos != null)
                    return pos;
            }
            return new InsertionPositionAssign(this, s, 0, false);
        }
	return null;
    }

    @Override
    public boolean Insert(InsertionPosition p)
    {
        if(!(p instanceof InsertionPositionAssign)) {
            return false;
        }
	InsertionPositionAssign assignInsert = (InsertionPositionAssign)p;
	if(assignInsert.m_bLhs) {
            IStatement s = p.GetStatement();
            if (s instanceof PStatementProperty || s instanceof PStatementVariable) {
                // Move the original stuff somewhere else
                IStatement stmt = GetStatement().GetLhs();
                if (stmt != null) {
                    CreateNullEvent(stmt, 0, m_lhs.getY());
                }
                // set up new thing
                GetStatement().SetLhs(p.GetStatement());
            }
        }
	else {
            // Move the original stuff somewhere else
            IStatement stmt = GetStatement().GetRhs();
            if (stmt != null && !(stmt instanceof PStatementConst || stmt instanceof PStatementNull)) {
                CreateNullEvent(stmt, 0, m_rhs.getY());
            }
            // set up new thing
            GetStatement().SetRhs(p.GetStatement());
        }
        return true;
    }

    @Override
    public void ClearHighlight()
    {
	if(jPnlLhs != null)
            jPnlLhs.ClearHighlight();
        if(jPnlRhs != null)
            jPnlRhs.ClearHighlight();
        super.ClearHighlight();
    }

    @Override
    public void DrawInsertionPosition(Graphics2D g)
    {
	assert m_highlight instanceof InsertionPositionAssign;
	InsertionPositionAssign assignInsert = (InsertionPositionAssign)m_highlight;
	
        if(assignInsert.m_bLhs)
            FrameComponent(m_lhs, g);
	else
            FrameComponent(m_rhs, g);
    }

    @Override
    protected void NotifyChildRemoved(JPnlLine child)
    {
	PStatementAssign s = GetStatement();
	if(jPnlLhs == child)
            s.SetLhs(null);
	else if(jPnlRhs == child)
            s.SetRhs(defaultConstStmt.clone());
    }

    @Override
    public void setEditable(boolean b)
    {
        if (jPnlLhs != null) {
            jPnlLhs.setEnabled(b);
            jPnlLhs.setEditable(b);
        }
        if (jPnlRhs != null) {
            jPnlRhs.setEnabled(b);
            jPnlRhs.setEditable(b);
        }
    }

    @Override
    public JPnlLineAssign clone()
    {
        return new JPnlLineAssign(GetStatement().clone(), m_l);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import javax.swing.*;
import pCSDT.Scripting.*;
import pCSDT.Utility;

/**
 *
 * @author Jason
 */
public class JPnlLineBinary extends JPnlLineDraggable
{
    static Image img;

    JPnlLine m_lLhs;
    JPanel m_opPanel = new JPanel();
    JLabel m_opName;
    JPnlLine m_lRhs;

    // default stmt
    static PStatementConst defaultCondStmt = new PStatementConst(1);

    JPnlPlaceholderExpr m_lhs = new JPnlPlaceholderExpr();
    JPnlPlaceholderExpr m_rhs = new JPnlPlaceholderExpr();

    public JPnlLineBinary(ePBinOpType op, LayoutInfo info)
    {
	this(new PStatementBinary(op), info);
    }

    public JPnlLineBinary(PStatementBinary s, LayoutInfo info)
    {
        super(s, info);
	setOpaque(false);
        setBackground(Color.yellow);
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletExpression2.png");
        }
    }

    @Override
    public void InitControls()
    {
        PStatementBinary s = GetStatement();

        IStatement lhsStmt = s.GetLhs();
	if(lhsStmt != null)
	{
            m_lLhs = lhsStmt.GetGui(m_l);
            m_lLhs.InitControls();
            m_lhs.add(m_lLhs);
            if (lhsStmt instanceof PStatementDynamicConst) {
                m_lhs.setEnabled(false);
            }
	}

	final String[] opNames = {
			PBinOpType.GetOpString(ePBinOpType.Add),
			PBinOpType.GetOpString(ePBinOpType.Subtract),
			PBinOpType.GetOpString(ePBinOpType.Multiply),
			PBinOpType.GetOpString(ePBinOpType.Divide),
			PBinOpType.GetOpString(ePBinOpType.Less),
			PBinOpType.GetOpString(ePBinOpType.LessEqual),
			PBinOpType.GetOpString(ePBinOpType.Equal),
			PBinOpType.GetOpString(ePBinOpType.NotEqual),
			PBinOpType.GetOpString(ePBinOpType.GreaterEqual),
			PBinOpType.GetOpString(ePBinOpType.Greater),
			PBinOpType.GetOpString(ePBinOpType.AND),
			PBinOpType.GetOpString(ePBinOpType.OR)
		};
        m_opName = new JLabel(opNames[s.GetOperator().ordinal()]);
        m_opName.setHorizontalAlignment(SwingConstants.CENTER);

        IStatement rhsStmt = s.GetRhs();
        if(rhsStmt != null)
	{
            m_lRhs = s.GetRhs().GetGui(m_l);
            m_lRhs.InitControls();
            m_rhs.add(m_lRhs);
            if (rhsStmt instanceof PStatementDynamicConst) {
                m_rhs.setEnabled(false);
            }
	}
        add(m_lhs);
        m_opPanel.setOpaque(false);
        m_opPanel.setLayout(new BorderLayout());
        m_opPanel.add(m_opName, BorderLayout.CENTER);
        add(m_opPanel);
        add(m_rhs);
	SetGridDimensions(3, 1, 4);

        // Default the size to the preferred size:
        setMinimumSize(getPreferredSize());
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(img, 0, 0, getWidth(), getHeight(), 0, 0, 349, 81, this);
        super.paintComponent(g);
    }

    @Override
    public PStatementBinary GetStatement() {return (PStatementBinary)super.GetStatement();}

    @Override
    public eSaveResult Save()
    {
	// Save the lhs and rhs:
	eSaveResult rs;
	if(m_lLhs != null)
        {
            rs = m_lLhs.Save();
            if(rs != eSaveResult.Success)
                return rs;
	}

	if(m_lRhs != null)
	{
            rs = m_lRhs.Save();
            if(rs != eSaveResult.Success)
                return rs;
	}

	// All done.
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
            // see if inner stuff wants it to be inserted
            if (m_lLhs != null) {
                InsertionPosition pos = m_lLhs.GetInsertionPosition(adj, s);
                if (pos != null)
                    return pos;
            }
            return new InsertionPositionBinary(this, s, 0, true);
        }
	adj = Utility.Transform(this, pt, m_rhs);
	if(m_rhs.isEnabled() && m_rhs.contains(adj)) {
            // see if inner stuff wants it to be inserted
            if (m_lRhs != null) {
                InsertionPosition pos = m_lRhs.GetInsertionPosition(adj, s);
                if (pos != null)
                    return pos;
            }
            return new InsertionPositionBinary(this, s, 0, false);
        }
	return null;
    }

    @Override
    public boolean Insert(InsertionPosition p)
    {
	if(!(p instanceof InsertionPositionBinary))
            return false;
		
	InsertionPositionBinary binInsert = (InsertionPositionBinary)p;
	if(binInsert.m_bLhs) {
            // create a null event for the original statements if any
            IStatement stmt = GetStatement().GetLhs();
            if (stmt != null && !(stmt instanceof PStatementConst || stmt instanceof PStatementNull)) {
                CreateNullEvent(stmt, 0, m_lhs.getY());
            }
            GetStatement().SetLhs(p.GetStatement());
        }
	else {
            // create a null event for the original statements if any
            IStatement stmt = GetStatement().GetRhs();
            if (stmt != null && !(stmt instanceof PStatementConst || stmt instanceof PStatementNull)) {
                CreateNullEvent(stmt, 0, m_rhs.getY());
            }
            GetStatement().SetRhs(p.GetStatement());
        }
	return true;
    }

    @Override
    public void ClearHighlight()
    {
	if(m_lLhs != null)
            m_lLhs.ClearHighlight();
        if(m_lRhs != null)
            m_lRhs.ClearHighlight();
        super.ClearHighlight();
    }

    @Override
    public void DrawInsertionPosition(Graphics2D g)
    {
	assert m_highlight instanceof InsertionPositionBinary;
	InsertionPositionBinary binInsert = (InsertionPositionBinary)m_highlight;
        if(binInsert.m_bLhs)
            FrameComponent(m_lhs, g);
        else
            FrameComponent(m_rhs, g);
    }

    @Override
    protected boolean ChildRemoveAllowed(JPnlLine child)
    {
	////return child != jPnlLhs;
        // disallow drag of default JPnlLineConst
        if (child == m_lLhs && m_lLhs instanceof JPnlLineConst ||
            child == m_lRhs && m_lRhs instanceof JPnlLineConst) {
            return false;
        }
        return true;
    }

    @Override
    protected void NotifyChildRemoved(JPnlLine child)
    {
	PStatementBinary s = GetStatement();
	if(m_lLhs == child)
            s.SetLhs(defaultCondStmt.clone());
            //s.SetLhs(null);
	else if(m_lRhs == child)
            s.SetRhs(defaultCondStmt.clone());
            //s.SetRhs(null);
    }

    @Override
    public void setEditable(boolean b)
    {
        if(m_lLhs != null) {
            m_lLhs.setEnabled(b);
            m_lLhs.setFocusable(b);
            m_lLhs.setEditable(b);
        }
	if(m_lRhs != null) {
            m_lRhs.setEnabled(b);
            m_lRhs.setFocusable(b);
            m_lRhs.setEditable(b);
        }
    }

    @Override
    public JPnlLineBinary clone()
    {
	return new JPnlLineBinary(GetStatement().clone(), m_l);
    }
}

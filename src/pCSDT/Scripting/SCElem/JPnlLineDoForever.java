/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import pCSDT.Scripting.IStatement;
import pCSDT.Scripting.PStatementDoForever;
import pCSDT.Utility;

/**
 * This is the GUI representation of PStatementDoForever
 * @author tylau
 */
public class JPnlLineDoForever extends JPnlLineControl {

    JPanel m_descLine = new JPanel();
    JPnlPlaceholderStmt m_body = new JPnlPlaceholderStmt();

    JPnlLine m_lBody;

    public JPnlLineDoForever(LayoutInfo l)
    {
        this(new PStatementDoForever(), l);
    }

    public JPnlLineDoForever(PStatementDoForever s, LayoutInfo l)
    {
        super(s, l);
    }

    @Override
    public void InitControls()
    {
        SetDefaultBorder("", null);

     //Set up the header for this codelet
        m_descLine.setLayout(new SpringLayout());
        m_descLine.setOpaque(false);
        m_descLine.add(new JLabel("Do Forever"));
        SetGridDimensions(m_descLine, 1, 1, 5);
        add(m_descLine);
    
     //GetStatement grabs a PStatementDoForever associated with this JPnlLine
     //GetBodies grabs the PStatementList associated with the inside of the PStatementDF
     //GetGui returns the JPnlLineList associated with that PStatementList
     //JPnlLineList extends JPnlLine, so m_lBody is one JPnlLine representing all of the JPnlLines in the body of this JPnlDF
        m_lBody = GetStatement().GetBodies()[0].GetGui(m_l);
        m_lBody.InitControls();
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
        
     // Put everything into a grid in this codelet
        SetGridDimensions(1, 3, 3);

        // Default the size to the preferred size, with border allowance
        Dimension s = getPreferredSize();
        setMinimumSize(new Dimension(s.width+2*BORDERWIDTH, s.height+2*BORDERWIDTH));
    }

    @Override
    public void ClearHighlight()
    {
        if (m_lBody != null) {
            m_lBody.ClearHighlight();
        }
        super.ClearHighlight();
    }

    @Override
    public eSaveResult Save() {
        m_lBody.Save();
        return eSaveResult.Success;
    }

    @Override
    public void setEditable(boolean b) {
        m_lBody.setEditable(b);
    }

    @Override
    public PStatementDoForever GetStatement() {
        return (PStatementDoForever)m_statement;
    }

    @Override
    public JPnlLine GetCondition() {
        return null;
    }

    @Override
    public Container GetConditionContainer() {
        return null;
    }

    @Override
    public int GetConditionOffsetY() {
        return 0;  // not applicable
    }

    @Override
    public JPnlLine[] GetBodies() {
        return new JPnlLine[]{m_lBody};
    }

    @Override
    public Container[] GetBodyContainers() {
        return new Container[]{m_body};
    }

    @Override
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        Point adj;

        for (JPnlLine body: GetBodies()) {
            if (body == null) {
                continue;
            }
            // spatial transform and content vertification
            adj = Utility.Transform(this, pt, body);
            if (body.contains(adj)) {
                // The body contains the point. This means that we need to descend the body
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
                            this, s, Math.min(adj.y, getHeight()-adj.y),
                            GetStatement(),
                            adj.y - 0 < getHeight() - adj.y? InsertionPositionControl.RelTo.BodyFirst: InsertionPositionControl.RelTo.BodyLast));
                }
            }
        }
        // Give up
        return null;
    }

    @Override
    public JPnlLineDoForever clone() {
        return new JPnlLineDoForever(GetStatement().clone(), m_l);
    }
}

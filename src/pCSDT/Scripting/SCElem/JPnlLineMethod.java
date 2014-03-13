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
public class JPnlLineMethod extends JPnlLineDraggable
{
    static Image img;
    JPnlPlaceholderExpr[] placeholders;
    JPnlLine[] userInput;

    public JPnlLineMethod(PStatementMethod s, LayoutInfo info)
    {
        super(s, info);
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletMethod.png");
        }
        setOpaque(false);
    }

    public void InitControls()
    {
        PStatementMethod s = GetStatement();
        PMethod pMethod = s.GetMethod();

	SetDefaultBorder(pMethod.GetName()+"()", Color.WHITE);
                      
        String[] argNames = pMethod.GetArgNames();
        String[] argDescs = pMethod.GetArgDescs();
        IStatement[] args = s.GetChildren();
        placeholders = new JPnlPlaceholderExpr[argNames.length];
        userInput = new JPnlLine[argNames.length];

        if (argNames.length > 0) {
            for(int i = 0; i < argNames.length; i++)
            {               
                JLabel argname = new JLabel(argNames[i]);
                argname.addMouseListener(this);
                argname.addMouseMotionListener(this);
                if (argNames.length == argDescs.length) {
                    argname.setToolTipText(argDescs[i]);
                }
                argname.setForeground(Color.WHITE);
                add(argname);

                JPnlLine arg = args[i].GetGui(m_l);
                arg.InitControls();

                userInput[i] = arg;
                placeholders[i] = new JPnlPlaceholderExpr();

                IStatement stmt = s.GetArgs()[i];
                if (stmt != null) {
                    userInput[i] = stmt.GetGui(m_l);
                    userInput[i].InitControls();
                    placeholders[i].add(userInput[i]);
                }
                add(placeholders[i]);
            }
            SetGridDimensions(2, argNames.length, 2, 5, 2, 5);
        }
        else {
            // insert at least one stuff, height set to a very small value
            JPanel emptyPanel = new JPanel();
            emptyPanel.setPreferredSize(new Dimension(emptyPanel.getPreferredSize().width, 1));
            emptyPanel.setMaximumSize(new Dimension(emptyPanel.getMaximumSize().width, 1));
            emptyPanel.setOpaque(false);
            add(emptyPanel);
            SetGridDimensions(1,1);
        }

        setMinimumSize(getPreferredSize());
    }

    /**
     * This method tells how to draw this component.
     * @param g Graphic object
     */
    @Override
    public void paintComponent (Graphics g)
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
	for(JPnlLine cur : userInput)
	{
            rs = cur.Save();
            if(rs != eSaveResult.Success)
		return rs;
        }
        return eSaveResult.Success;
    }

    @Override
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        validate();  // make sure the layout is updated

        /*
		return super.GetInsertionPosition(pt, s);
         */
        Point adj;

	// Expression must have an evaluation:
	if(!s.HasReturnValue())
            return null;

        for (int i = 0; i < userInput.length; i++) {
            adj = Utility.Transform(this, pt, placeholders[i]);
            if(placeholders[i].contains(adj)) {
                // see if inner stuff wants it to be inserted
                if (userInput[i] != null) {
                    InsertionPosition pos = userInput[i].GetInsertionPosition(adj, s);
                    if (pos != null)
                        return pos;
                }
                return new InsertionPositionMethod(this, s, 0, i);
            }
        }
	return null;
    }

    @Override
    public boolean Insert(InsertionPosition p) {
        ////return false;
        if(!(p instanceof InsertionPositionMethod))
            return false;

        InsertionPositionMethod methodInsert = (InsertionPositionMethod)p;
        // create a null event for the original statements if any
        int argIdx = methodInsert.GetArgIdx();
        IStatement stmt = GetStatement().args[argIdx];
        if (stmt != null && !(stmt instanceof PStatementConst || stmt instanceof PStatementNull)) {
            Point p1 = getLocationOnScreen();
            Point p2 = userInput[argIdx].getLocationOnScreen();
            CreateNullEvent(stmt, 0, p2.y-p1.y);
        }
        GetStatement().args[methodInsert.GetArgIdx()] = p.GetStatement();
        return true;
    }

    @Override
    public void ClearHighlight()
    {
        for (JPnlLine line: userInput) {
            line.ClearHighlight();
        }
	super.ClearHighlight();
    }

    @Override
    public void DrawInsertionPosition(Graphics2D g) {
        ////super.DrawInsertionPosition(g);
        assert m_highlight instanceof InsertionPositionMethod;
	InsertionPositionMethod methodInsert = (InsertionPositionMethod)m_highlight;
	FrameComponent(placeholders[methodInsert.GetArgIdx()], g);
    }

    @Override
    protected boolean ChildRemoveAllowed(JPnlLine child)
    {
	////return child != jPnlLhs;
        // disallow drag of default JPnlLineConst and DropDownConst
        for (JPnlLine line: userInput) {
            if (child == line && 
                    (line instanceof JPnlLineConst || line instanceof JPnlLineDropDownConst)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void NotifyChildRemoved(JPnlLine child) {
        ////super.NotifyChildRemoved(child);
        PStatementMethod s = GetStatement();
        for (int i = 0; i < placeholders.length; i++) {
            if (userInput[i] == child) {
                // instead of setting null, set to default arg const value
                s.SetArg(i, s.GetDefaultArg(i));
            }
        }
    }

    @Override
    public PStatementMethod GetStatement() {return (PStatementMethod)m_statement;}

    @Override
    public JPnlLineMethod clone()
    {
        return new JPnlLineMethod(GetStatement().clone(), m_l);
    }

    @Override
    public void setEditable(boolean b)
    {
	for(JPnlLine cur : userInput)
            cur.setEditable(b);
    }

    public void SetUserInputsEnabled(boolean b)
    {
        //// disable the userInput
        for (int i = 0; i < userInput.length; i++) {
            userInput[i].setEnabled(b);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import pCSDT.Scripting.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import pCSDT.Utility;
import resource.layouts.SpringUtilities;

/**
 *
 * @author Jason Sanchez
 */
public class JPnlLineEvent extends JPnlLineDraggable
{
    static Image img, imgDisabled;
    static Color COLORDISABLED = new Color(50,50,50);
    PEvent m_evt;
    JPnlPlaceholderExpr[] placeholders;  // placeholder for parameters
    JPnlLine[] userInputs;  // JPnlLine corresponding to parameters
    IStatement[] args;  // IStatements corresponding to parameters
    JPnlLineList m_lLineList;
    boolean m_bIsBlank = false;
    JPanel topPanel;
    GridBagConstraints c = new GridBagConstraints();

    // This controls how much y-slack space there is off of the bottom of the line event
    public static final int ySlack = 40;

    /**
     * Constructor
     * @param evt
     * @param l
     */
    public JPnlLineEvent(PEvent evt, LayoutInfo l)
    {
        super(null, l);
        m_evt = evt;
        if (img == null) {
            img = GetImage("/resource/layouts/imgs/codeletEvent.png");
        }
        if (imgDisabled == null) {
            imgDisabled = GetImage("/resource/layouts/imgs/codeletEvent_disable.png");
        }
        args = m_evt.GetArgs();
        topPanel = GetTopLabel();

        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new GridBagLayout());
    }

    private JPanel GetTopLabel() {
        JPanel panel = new JPanel();
        titledBorder = new TitledBorder(new EmptyBorder(0,0,0,0), m_evt.toString(),
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Century Gothic", Font.BOLD, 10), Color.WHITE);
        panel.setBorder(titledBorder);
        PEventListUnbound elu = m_evt.GetEventList().GetUnboundEventList();

        if (elu != null) {
            String[] argNames = elu.GetArgNames();
            String[] argDescs = elu.GetArgDesc();
            placeholders = new JPnlPlaceholderExpr[argNames.length];
            userInputs = new JPnlLine[argNames.length];
            if (argNames.length > 0) {
                placeholders = new JPnlPlaceholderExpr[argNames.length];
                userInputs = new JPnlLine[argNames.length];
                for (int i = 0; i < argNames.length; i++) {
                    JLabel argname = new JLabel(argNames[i]);
                    argname.addMouseListener(this);
                    argname.addMouseMotionListener(this);
                    if (argNames.length == argDescs.length) {
                        argname.setToolTipText(argDescs[i]);
                    }
                    argname.setForeground(Color.WHITE);
                    panel.add(argname);

                    // new impl
                    
                    JPnlLine arg = args[i].GetGui(m_l);
                    arg.InitControls();
                    userInputs[i] = arg;
                    placeholders[i] = new JPnlPlaceholderExpr();

                    IStatement stmt = m_evt.GetArgs()[i];
                    if (stmt != null) {
                        userInputs[i] = stmt.GetGui(m_l);
                        userInputs[i].InitControls();
                        placeholders[i].add(userInputs[i]);
                        if (stmt instanceof PStatementDynamicConst) {
                            placeholders[i].setEnabled(false);
                        }
                    }
                    panel.add(placeholders[i]);
                }
                panel.setOpaque(false);

                // TODO: don't know why elements are not aligned properly
                // when there is only one argument, so make it a special case
                if (argNames.length == 1) {
                    panel.setLayout(new FlowLayout());
                }
                else {
                    panel.setLayout(new SpringLayout());
                    SpringUtilities.makeCompactGrid(panel, 2, argNames.length, 2, 5, 2, 5);
                }
                panel.setMinimumSize(new Dimension(150, panel.getPreferredSize().height));
                panel.setPreferredSize(new Dimension(150, panel.getPreferredSize().height));
            }
            else {
                placeholders = new JPnlPlaceholderExpr[0];
                userInputs = new JPnlLine[0];
                panel.setOpaque(false);
                panel.setLayout(new SpringLayout());
                SpringUtilities.makeCompactGrid(panel, 2, argNames.length, 2, 5, 2, 5);
                panel.setMinimumSize(new Dimension(150, 22));
                panel.setPreferredSize(new Dimension(150, 22));
            }
        }
        else {
            panel.setOpaque(false);
            panel.setLayout(new BorderLayout());
            panel.setMinimumSize(new Dimension(150, 22));
            panel.setPreferredSize(new Dimension(150, 22));

        }
        return panel;
    }

    /**
     * Clear the associated m_lLineList to null
     */
    public void ClearLineList()
    {
        this.m_lLineList = null;
    }

    /**
     * Sets this event to be a blank (unbounded) event
     * @param bIsBlank Set to true for a blank event, so that child statements will not be drawn.
     */
    public void SetBlank(boolean bIsBlank)
    {
	m_bIsBlank = bIsBlank;
    }

    private void InitTopLabel() 
    {
        ////topPanel.add(lbl, BorderLayout.CENTER);
        args = m_evt.GetArgs();
        topPanel = GetTopLabel();
        add(topPanel, c);
        c.gridy++;
    }

    @Override
    public void InitControls()
    {
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.insets = new Insets(0,0,0,0);
        c.fill = GridBagConstraints.BOTH;
        
        InitTopLabel();

        // Construct the statement list that follows the label:
        c.insets = new Insets(-5,0,0,0);
        IStatementList l = m_evt.GetListener();
	int iNComponents = 1;
	if(!m_bIsBlank && l != null && l.GetChildren().length != 0)
	{
            m_lLineList = (JPnlLineList)l.GetGui(m_l);
            m_lLineList.InitControls();
            add(m_lLineList, c);
            c.gridy++;
            iNComponents++;
	}

	// Set up the grid:
	setSize(getPreferredSize());
     }

    /**
     * InitControls with no display of associated script
     * For use in JPnlEventMgr
     */
    public void InitControlsCompact()
    {
        InitTopLabel();
	// Default the size to the preferred size:
	setSize(getPreferredSize());
     }

    @Override
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s)
    {
        validate();  // make sure the layout is updated

        if (pt.x < 0 || pt.x > this.getWidth() || pt.y < 0 || pt.y > this.getHeight()+ySlack) {
            return null;
        }

        // from JPnlLinemethod
        Point adj;
        for (int i = 0; userInputs != null && i < userInputs.length; i++) {
            adj = Utility.Transform(this, pt, placeholders[i]);
            if(placeholders[i].isEnabled() && placeholders[i].contains(adj)) {
                // see if inner stuff wants it to be inserted
                if (userInputs[i] != null) {
                    InsertionPosition pos = userInputs[i].GetInsertionPosition(adj, s);
                    if (pos != null) {
                        return pos;
                    }
                    // if not, it is the placeholder who is taking the insertion
                    if (s.HasReturnValue()) {
                        return new InsertionPositionEvent(this, s, 0, i);
                    }
                }
            }
        }
        // end

	if(m_lLineList == null)
            if(s.HasSideEffect())
                return new InsertionPositionEvent(this, s, 0, false);
            else
                return null;

	// See if the line list would like to accept this insertion position:
	InsertionPosition pos = m_lLineList.GetInsertionPosition(Utility.MakeRelative(pt, m_lLineList), s);
	if(pos != null)
            return pos;

	// Statement must have a side effect to go into an event.
	if(!s.HasSideEffect())
            return null;

	// Insertion allowed, determine whether the point occurs before or after the top bound of
	// the line list:
	return new InsertionPositionEvent(
			this,
			s,
			Math.abs(pt.y - m_lLineList.getY()), pt.y < m_lLineList.getY()
		);
    }

    @Override
    public boolean Insert(InsertionPosition insert)
    {
	if(insert instanceof InsertionPositionEvent)
	{
            InsertionPositionEvent iEvt = (InsertionPositionEvent)insert;
            int argIdx = iEvt.GetArgIdx();
            if (argIdx > -1) {
                IStatement stmt = args[argIdx];
                if (stmt != null && !(stmt instanceof PStatementConst || stmt instanceof PStatementNull)) {
                    Point p1 = getLocationOnScreen();
                    Point p2 = userInputs[argIdx].getLocationOnScreen();
                    CreateNullEvent(stmt, 0, p2.y-p1.y);
                }
                args[argIdx] = insert.GetStatement();
                ReinitControls();
                return true;
            }
            else {
                IStatementList l = m_evt.GetListener();
                if(iEvt.m_bFirst)
                    l.InsertChild(insert.GetStatement(), 0);
                else
                    l.AppendChild(insert.GetStatement());
                ReinitControls();
                return true;
            }
	}
	return false;
    }

    @Override
    public void Highlight(InsertionPosition highlight)
    {
        if(m_highlight != null)
            return;
	m_highlight = highlight;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        DrawComponent(g);
        super.paintComponent(g);
    }

    public void DrawComponent(Graphics g)
    {
        if (isEnabled()) {
            g.drawImage(img, 0, 0, topPanel.getWidth(), 5, 0, 0, 360, 10, this);
            g.drawImage(img, 0, 5, topPanel.getWidth(), topPanel.getHeight()-5, 0, 11, 360, 70, this);
            g.drawImage(img, 0, topPanel.getHeight()-5, topPanel.getWidth(), topPanel.getHeight(), 0, 71, 360, 80, this);
            ////lbl.setForeground(Color.WHITE);
        }
        else {
            g.drawImage(imgDisabled, 0, 0, topPanel.getWidth(), 5, 0, 0, 360, 10, this);
            g.drawImage(imgDisabled, 0, 5, topPanel.getWidth(), topPanel.getHeight()-5, 0, 11, 360, 70, this);
            g.drawImage(imgDisabled, 0, topPanel.getHeight()-5, topPanel.getWidth(), topPanel.getHeight(), 0, 71, 360, 80, this);
            ////lbl.setForeground(COLORDISABLED);
        }
    }

    /**
     *
     * @return True, because an event may always be removed
     */
    @Override
    public boolean RemoveAllowed() {return true;}

    /**
     * Executes a removal without notifying any parents, as this control would not have
     * any parents.
     */
    @Override
    public void Remove()
    {
        getParent().remove(this);
    }

    /**
     * Overridden because this line is always atomic and never as any ancestors.
     * @return this
     */
    @Override
    public JPnlLine GetFirstAtomicAncestor()
    {
	return this;
    }

    /**
     * This method will return null because a JPnlLineEvent cannot have a typed ancestor
     *
     * @return null
     */
    @Override
    public JPnlLine GetLineParent()
    {
	assert super.GetLineParent() == null;
	return null;
    }

    @Override
    public void ClearHighlight()
    {
        if (userInputs != null) {
            for (JPnlLine line: userInputs) {
                line.ClearHighlight();
            }
        }
	if(m_lLineList != null)
            m_lLineList.ClearHighlight();
        super.ClearHighlight();
    }

    @Override
    public void DrawInsertionPosition(Graphics2D g)
    {
	if(m_highlight == null || !(m_highlight instanceof InsertionPositionEvent))
            return;

        InsertionPositionEvent evtInsert = (InsertionPositionEvent)m_highlight;
        int idx = evtInsert.GetArgIdx();
        if (idx > -1) {
            FrameComponent(placeholders[evtInsert.GetArgIdx()], g);
        }
        else {
            DrawHorizontalHighlight(g, topPanel.getHeight()-5);
        }
    }

    @Override
    protected boolean ChildRemoveAllowed(JPnlLine child)
    {
	////return child != jPnlLhs;
        // disallow drag of default JPnlLineConst and DropDownConst
        for (JPnlLine line: userInputs) {
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
        for (int i = 0; i < placeholders.length; i++) {
            if (userInputs[i] == child) {
                // instead of setting null, set to default arg const value
                m_evt.SetArg(i, m_evt.GetDefaultArg(i));
            }
        }
    }


    @Override
    public eSaveResult Save()
    {
	if(m_evt == null)
            return eSaveResult.Success;

        eSaveResult rs;
        for (JPnlLine cur: userInputs) {
            rs = cur.Save();
            if (rs != eSaveResult.Success)
                return rs;
        }

	m_evt.SetCoords(new Point3D(getLocation(), m_evt.GetCoords().z));
        m_evt.Register();
	return m_lLineList.Save();
    }

    @Override
    public JPnlLineEvent clone()
    {
	JPnlLineEvent retVal = new JPnlLineEvent(m_evt.clone(), m_l);
        retVal.SetBlank(m_bIsBlank);
        return retVal;
    }

    public PEvent GetPEvent() {return m_evt;}

    @Override
    public void setEditable(boolean b)
    {
	if(m_evt == null)
            return;
	m_lLineList.setEditable(b);
    }
}

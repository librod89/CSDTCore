/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Scripting.SCElem;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import pCSDT.Presentation.JPnlDragParticipant;
import pCSDT.Presentation.JPnlScriptlet;
import pCSDT.Scripting.*;
import pCSDT.Utility;
import resource.layouts.SpringUtilities;

/**
 * This is a single line of codelet
 * @author Jason
 */
public abstract class JPnlLine extends JPanel
{
    public static final int BORDERWIDTH = 3;

    JPnlDragParticipant p;  // the JPnlDragParticipant that it is attached to
    // This is the statement described by the line:
    protected IStatement m_statement;
    LayoutManager m_layout;
    protected LayoutInfo m_l;

    boolean m_removable = true;

    // Hit test result, if highlighting is selected, or Miss if no highlighting is requested.
    protected InsertionPosition m_highlight;

    // default const stmt
    static PStatementConst defaultConstStmt = new PStatementConst(1);

    // reference to titledBorder
    protected TitledBorder titledBorder;

    // This is a debug flag used for testing hit-test features:
    public static final boolean m_bDebugDraw = true;

    static Image imgHorizontalHighlight;

    public void FrameComponent(Component c, Graphics2D g)
    {
	g.setStroke(new BasicStroke(4));
	g.setColor(Color.GREEN);
	Point target = c.getLocationOnScreen();
        Point root = this.getLocationOnScreen();
        g.drawRect(target.x-root.x, target.y-root.y, c.getWidth(), c.getHeight());
    }

    public enum eSaveResult
    {
        Success,
        Failure,
        NotImplemented
    };

    public JPnlLine(IStatement statement, LayoutInfo l)
    {
	this(statement, l, new SpringLayout());
    }

    public JPnlLine(IStatement statement, LayoutInfo l, LayoutManager layout)
    {
        m_statement = statement;
        m_l = l;
        m_layout = layout;

        if (imgHorizontalHighlight == null) {
            imgHorizontalHighlight = GetImage("/resource/layouts/imgs/Line.png");
        }
        
	// Default layout and opacity:
        setLayout(layout);
	setOpaque(false);

	// Lower bound the preferred size:
	setMinimumSize(new Dimension(30, 14));
    }

    protected void SetGridDimensions(int cx, int cy)
    {
	SetGridDimensions(this, cx, cy);
    }

    protected void SetGridDimensions(int cx, int cy, int insets)
    {
        SetGridDimensions(this, cx, cy, insets);
    }

    protected void SetGridDimensions(int cx, int cy, int initx, int inity, int padx, int pady)
    {
        SetGridDimensions(this, cx, cy, initx, inity, padx, pady);
    }

    protected void SetGridDimensions(Container c, int cx, int cy)
    {
	SpringUtilities.makeCompactGrid(c, cy, cx, 0, 0, 0, 0);
        updateUI();
    }

    protected void SetGridDimensions(Container c, int cx, int cy, int insets)
    {
	SpringUtilities.makeCompactGrid(c, cy, cx, insets, insets, insets, insets);
        updateUI();
    }

    protected void SetGridDimensions(Container c, int cx, int cy, int initx, int inity, int padx, int pady)
    {
	SpringUtilities.makeCompactGrid(c, cy, cx, initx, inity, padx, pady);
        updateUI();
    }

    @Override
    public Dimension getPreferredSize()
    {
	Dimension prefSize = super.getPreferredSize();
        Dimension minSize = getMinimumSize();
        if (titledBorder == null) {
            return new Dimension(
                    Math.max(prefSize.width, minSize.width),
                    Math.max(prefSize.height, minSize.height)
            );
        }
        else {
            Dimension borderMinSize = titledBorder.getMinimumSize(this);
            return new Dimension(
                    Math.max(prefSize.width, Math.max(minSize.width,borderMinSize.width+30)),
                    Math.max(prefSize.height, minSize.height)
            );
        }
    }

    /**
     * This is a convenience method that allows compact conditional loading.
     *
     * @param img The function first tests to see if this is non-null.  If it is null, the
     * method simply returns {@code img}.  This is provided for convenience only.
     * @param src This is the image source to be loaded if {@code img} is null.
     * @return The resource loaded from {@code src}, or null if the resource could not be loaded.
     */
    public Image GetImage(String src)
    {
	try
	{
            URL myurl = JPnlLine.class.getResource(src);
            Toolkit tk = getToolkit();
            if (tk != null) {
                return tk.getImage(myurl);
            }
	}
	catch(Exception ex) {
            ex.printStackTrace();
        }
	return null;
    }

    /**
     * Set default border of the codelet
     * @param title the title of the codelet
     * @param color the color of the border, null if no border needed
     */
    public void SetDefaultBorder(String title, Color color)
    {
        if (color != null) {
            titledBorder = new TitledBorder(new EmptyBorder(0,0,0,0), title,
                    javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Century Gothic", Font.BOLD, 10),
                    color);
        }
        else {
            titledBorder = new TitledBorder(new EmptyBorder(0,0,0,0), "");
        }
        setBorder(titledBorder);
    }

    /**
     * Called to initialize all child controls
	 * 
     * @param parent The parent control of this codelet line
     */
    public abstract void InitControls();

    /**
     * Called when it is time to save all of the elements in all of the controls.
	 *
     * @return A member of the eSaveResult enumeration indicating success or a failure reason
     */
    public abstract eSaveResult Save();

    /**
     * Called when the codelet's editability should be adjusted
     *
     * @param b True if the codelet should be editable; false otherwise.
     */
    public abstract void setEditable(boolean b);

    /**
     * This is a convenience method that reinitializes the control based on the bound event
     *
     * If anything more complex than removing all children and invoking InitControls(parent)
     * must be done, then this method should be overridden in the derived class.
     */
    public void ReinitControls()
    {
	// Remove all children and reinitialize.
	removeAll();
	InitControls();
	updateUI();
    }

    /**
     * Set whether it is allowed to be removed
     * @param b
     */
    public void SetRemoveAllowed(boolean b) {
        this.m_removable = b;
    }

    /**
     * Called when a drag source wants to know if a particular line may be dragged
     * @return True if the line may be dragged
     */
    public boolean RemoveAllowed()
    {
	JPnlLine parent = GetLineParent();
	assert parent != null;
	return parent.ChildRemoveAllowed(this);
    }

    /**
     * Called when a drag source is requesting that a particular child be dragged
     *
     * @param child The child to be dragged
     * @return True to indicate that permission is granted, false otherwise
     */
    protected boolean ChildRemoveAllowed(JPnlLine child) {return true;}

    /**
     * Removes this line from its parent container.
     */
    public void Remove()
    {
	assert RemoveAllowed();

	// If the parent is defined, notify it that a child has been removed.
	JPnlLine parent = GetLineParent();
	assert parent != null;
	assert parent.ChildRemoveAllowed(this);
	parent.NotifyChildRemoved(this);
        parent.doLayout();
        
	// Do this no matter what:
	Container  p = getParent();
	if(p != null)
            p.remove(this);
    }

    /**
     * Notifies the control that the given child is about to be removed.
     *
     * The parent then updates the statement tree based on the child being removed from
     * it.  The default implementation does nothing.
     *
     * @param child The child being removed
     */
    protected void NotifyChildRemoved(JPnlLine child)
    {
	assert ChildRemoveAllowed(child);
    }

    /**
     * Returns the first ancestor that may be independently handled
     * @return The first atomic ancestor
     */
    public JPnlLine GetFirstAtomicAncestor()
    {
        if(RemoveAllowed())
            return this;
	JPnlLine parent = GetLineParent();
	assert parent != null;
	return parent.GetFirstAtomicAncestor();
    }

    /**
     * @return The first ancestor of this line which is also a line.
     */
    public JPnlLine GetLineParent()
    {
	return Utility.GetTypedAncestor(JPnlLine.class, getParent());
    }

    /**
     *
     * @return The event ancestor rooting this control
     */
    public JPnlLineEvent GetRoot()
    {
	return Utility.GetTypedAncestor(JPnlLineEvent.class, this);
    }

    /**
     * Gets insertion position for a given statement at a given point
     *
     * This function gets the insertion position corresponding to a given statement and
     * line.  It is expected that derived classes will descend their statement tree in
     * order to find the most appropriate insertion point.
     *
     * @param pt The point, relative to this line, where insertion is to take place.
     * @param s The statement the user is requesting be inserted.
     * @return An InsertionPosition construct.
     */
    public InsertionPosition GetInsertionPosition(Point pt, IStatement s) {return null;}

    /**
     * Performs the actual insertion operation
     * @param insert The value to be inserted
     * @return True to indicate that the insertion has taken place
     */
    public boolean Insert(InsertionPosition insert) {return false;}

    /**
     *
     * For placing a statement in a PEventNull next to the original line.
     * Default is right next to the line.
     * One can specify additional x and y offsets
     * @param stmt - the PStatement to be inserted into the null event
     * @param addxoffset - the additional x offset
     * @param addyoffset - the additional y offset
     */
    protected void CreateNullEvent(IStatement stmt, int addxoffset, int addyoffset) {
        // use the parent PEvent to figure out the PObject
        JPnlLineEvent parentEvt = Utility.GetTypedAncestor(JPnlLineEvent.class, this);
        PObject m_boundObj = parentEvt.GetPEvent().GetPObject();
        // create a null event for the PObject
        PEventNull nullRoot = m_boundObj.CreateNullEvent();
        nullRoot.GetListener().AppendChild(stmt);
        // set up a good position for the null event
        Point los = getLocationOnScreen();
        Point elos = parentEvt.getLocationOnScreen();
        int offsety = los.y - elos.y;
        Point evtLoc = parentEvt.getLocation();
        Point3D c = new Point3D(evtLoc.x + parentEvt.getWidth() + addxoffset,
                evtLoc.y + offsety + addyoffset);
        Utility.GetTypedAncestor(JPnlScriptlet.class, this).BindEvent(nullRoot, c);
    }

    /**
     * Highlights the insertion position given by highlight
     * @param highlight The insertion position to be highlighted.
     */
    public void Highlight(InsertionPosition highlight)
    {
        if(highlight.equals(m_highlight))
            return;
        m_highlight = highlight;
        repaint();
    }

    /**
     * Clears any highlight assigned during a call to Highlight.
     */
    public void ClearHighlight()
    {
        if(m_highlight == null)
            return;
        m_highlight = null;
        repaint();
    }

    /**
     * This is the default insertion position drawing routine.
     *
     * This routine is invoked at the time the insertion position should be drawn.  It is the responsibility
     * of the derived class to determine whether drawing should take place, however, as this method is called
     * unconditionally.
     *
     * @param g The graphics context to draw in.
     */
    public void DrawInsertionPosition(Graphics2D g) {}

    /**
     * Draws a horizontal line for highlight indication for the x-extent of the control
     *
     * @param g The graphics context to draw in.
     * @param linePositionY The y-position of the insertion line
     */
    public void DrawHorizontalHighlight(Graphics2D g, int linePositionY)
    {
        g.drawImage(imgHorizontalHighlight, 0, linePositionY, getWidth(), 5, this);
       super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g)
    {
	super.paint(g);
	if(g instanceof Graphics2D && m_highlight != null)
            DrawInsertionPosition((Graphics2D)g);
    }

    /**
     * @return The statement bound by this line
     */
    public IStatement GetStatement() {return m_statement;}

    /**
     * @return A clone of this instance, which must be initialized with a call to Initialize
     */
    @Override
    public abstract JPnlLine clone();
}

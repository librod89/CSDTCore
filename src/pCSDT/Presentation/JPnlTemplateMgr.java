/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pCSDT.Presentation;

import java.awt.*;
import java.awt.event.*;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;
import pCSDT.Utility;

/**
 * This generalizes a class of codelet template managers which has interaction
 * with JPnlScriptlet JPanel.
 * @author tylau
 */
public abstract class JPnlTemplateMgr extends JPnlDragParticipantP
{
    protected JPnlScriptlet jpnlScriptlet;  // handle to JPnlScriptlet

    protected JPnlTemplateMgr(LayoutInfo l, JPnlScriptlet jpnlScriptlet)
    {
        super(l);
        this.jpnlScriptlet = jpnlScriptlet;
        ////setAutoscrolls(true); //enable synthetic drag events
        addMouseMotionListener(this); //handle mouse drags
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        // if the current mouse pointer is over the JPnlScriptlet area,
	// also do the scroll there
	////int x_rel_scriptlet = e.getXOnScreen() - jpnlScriptlet.getLocationOnScreen().x;
	////int y_rel_scriptlet = e.getYOnScreen() - jpnlScriptlet.getLocationOnScreen().y;
	////if(x_rel_scriptlet >= 0 && x_rel_scriptlet <= jpnlScriptlet.getWidth())
	////{
            ////Rectangle r_scriptlet = new Rectangle(x_rel_scriptlet, y_rel_scriptlet, 1, 1);
            ////jpnlScriptlet.scrollRectToVisible(r_scriptlet);
	////}
	super.mouseDragged(e);
    }

    @Override
    public DragSource LocateDragSource(Point3D pt)
    {
        Component comp = findComponentAt(pt.Get2DPoint());
        JPnlLine m = Utility.GetTypedAncestor(JPnlLineDraggable.class, comp);
	if(m == null)
            return null;

	JPnlLine retVal = m.clone();
        retVal.setSize(m.getSize());
        retVal.InitControls();
        return new DragSource(retVal, pt.x - m.getX(), pt.y - m.getY());
    }

        @Override
    public void SetUpDragSource(DragSource ds) {
    }

    public void AddExpression(JPnlLine line, int gridy)
    {
	c.gridy = gridy;
        c.fill = GridBagConstraints.NONE;
	line.InitControls();
	line.setEditable(false);
	add(line, c);
    }

    @Override
    public void ReleaseDragSource(JPnlLine source)
    {
    }

    @Override
    public void BindObject(PObject obj)
    {
    }

    @Override
    public void StopDrag(Point3D pt, JPnlLine line)
    {
        if(line instanceof JPnlLineEvent)
	{
            JPnlLineEvent lineEvt = (JPnlLineEvent)line;
            // remove the (x,y) coordiates associated with the lineEvt
            lineEvt.GetPEvent().SetCoords(null);
            lineEvt.GetPEvent().DeRegister();
	}
    }

    @Override
    public void UpdateDrag(Point3D pt, JPnlLine line)
    {
    }
}

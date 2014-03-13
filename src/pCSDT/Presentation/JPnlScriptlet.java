/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPnlScriptlet.java
 * Created on Jun 4, 2009, 7:13:24 PM
 */
package pCSDT.Presentation;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import pCSDT.Scripting.*;
import pCSDT.Scripting.SCElem.*;
import java.util.*;
import pCSDT.Utility;

/**
 * This is a JPanel that displays a statement tree associated with a bounded
 * PEvent.
 * @author Jason
 */
public class JPnlScriptlet extends JPnlDragParticipantLP
	implements MouseMotionListener, ComponentListener
{
    static final int CODELET_INSET = 20; //Space between codelet groups

    static final int MARGIN_X = 40;  // x margin left on auto size-adjustment
    static final int MARGIN_Y = 40;  // y margin left on auto size-adjustment

    // list of JPnlLineEvent currently placed in JPnlScriptlet
    // sorted in layers
    java.util.ArrayList<JPnlLineEvent> m_lEvents = new java.util.ArrayList<JPnlLineEvent>();

    public void componentResized(ComponentEvent e) {
        // update the optimal size of the scriptlet panel
        SetOptimalSize();
        doLayout();
        updateUI();
    }

    public void componentMoved(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {}

    public void componentHidden(ComponentEvent e) {}

    // comparator to sort the added m_lEvents components by z-order
    private class JPnlEvtComparator implements Comparator<JPnlLineEvent> {
        JPnlScriptlet jpnlScriptlet;
        public JPnlEvtComparator(JPnlScriptlet jpnlScriptlet) {
            this.jpnlScriptlet = jpnlScriptlet;
        }
        @Override
        public int compare(JPnlLineEvent o1, JPnlLineEvent o2) {
            return jpnlScriptlet.scriptletPane.getLayer((Component)o2) - jpnlScriptlet.scriptletPane.getLayer((Component)o1);
        }
    }

    JPnlEvtComparator jpnlEvtComparator;
    
    PObject m_boundObj;
    InsertionPosition m_lastPos;

    /**
     * Default constructor
     */
    public JPnlScriptlet(LayoutInfo l)
    {
	super(l);
        jpnlEvtComparator = new JPnlEvtComparator(this);
        addComponentListener(this);
    }

    /**
     * This method clears the bounded PEvent, the associated statement tree
     * and the corresponding display on this JPanel.
     */
    public void Clear()
    {
	if(m_lEvents.isEmpty())
            return;

	m_lEvents.clear();
	scriptletPane.removeAll();
	scriptletPane.repaint();
    }

    public DragSource GenerateDragSource(Point3D pt, JPnlLine excludeLine)
    {
        // the event to be excluded from being dragged
        JPnlLine excludeEvent = null;
        if (excludeLine != null) {
            excludeEvent =
                    Utility.GetTypedAncestor(JPnlLineEvent.class, excludeLine);
        }
        // Trivial return check:
        // sort m_lEvents by layer - so later the selection order is correct
        java.util.Collections.sort(m_lEvents, jpnlEvtComparator);
        
	for(JPnlLineEvent lEvt : m_lEvents)
        {
            // Ask the event for the component at this point:
            Point rel = new Point(pt.x - lEvt.getX(), pt.y - lEvt.getY());

            // See if we can get a hit test on the object to be dragged:
            Component comp = lEvt.findComponentAt(rel);
            JPnlLine line = Utility.GetTypedAncestor(JPnlLine.class, comp);
            if(line == null)
                continue;

            line = line.GetFirstAtomicAncestor();

            // skip the given excludeLine
            if (line == excludeLine)
                continue;

            // skip any line that has the same event as the excludeLine
            if (Utility.GetTypedAncestor(JPnlLineEvent.class, line) == excludeEvent)
                continue;

            assert line != null;

            // Store the original size, because removing this line is going to screw
            // up the dimensions as there will no longer be a sizer to provide size
            // information.
            Dimension origSize = line.getSize();

            // We also have to store original offsets for a similar reason.  These need
            // to be computed from absolute positions because the located line may be
            // nested in several containers down from the event.
            Point tAbs = lEvt.getLocationOnScreen();
            Point lAbs = line.getLocationOnScreen();
            int offsetX = tAbs.x + rel.x - lAbs.x;
            int offsetY = tAbs.y + rel.y - lAbs.y;

            // Remove the targeted line.
            line.Remove();
            line.setSize(origSize);

            if(line == lEvt) {
                m_lEvents.remove(lEvt);
            }
            else if(lEvt instanceof JPnlLineEventNull && lEvt.GetPEvent().IsEmpty()) {
		// Event is a (now empty) null event that now must be removed
		UnbindEvent(lEvt);
            }
            else {
		// Event must be reinitialized.
                // Reset lEvt to have no JPnlLineList if lEvt is now empty
                // (having no statement attached to it)
                if (lEvt.GetPEvent().IsEmpty())
                    lEvt.ClearLineList();
                lEvt.ReinitControls();
            }

            // Update and repaint, then return the removed line.
            scriptletPane.updateUI();
            return new DragSource(line, offsetX, offsetY);
	}
        return null;
    }

    public DragSource LocateDragSource(Point3D pt)
    {
        // Trivial return check:
        // sort m_lEvents by layer - so later the selection order is correct
        java.util.Collections.sort(m_lEvents, jpnlEvtComparator);

	for(JPnlLineEvent lEvt : m_lEvents)
        {
            // Ask the event for the component at this point:
            Point rel = new Point(pt.x - lEvt.getX(), pt.y - lEvt.getY());

            // See if we can get a hit test on the object to be dragged:
            Component comp = lEvt.findComponentAt(rel);
            JPnlLine line = Utility.GetTypedAncestor(JPnlLineDraggable.class, comp);
            if(line == null)
                continue;

            line = line.GetFirstAtomicAncestor();

            assert line != null;

            // We also have to store original offsets for a similar reason.  These need
            // to be computed from absolute positions because the located line may be
            // nested in several containers down from the event.
            Point tAbs = lEvt.getLocationOnScreen();
            Point lAbs = line.getLocationOnScreen();
            int offsetX = tAbs.x + rel.x - lAbs.x;
            int offsetY = tAbs.y + rel.y - lAbs.y;

            return new DragSource(line, offsetX, offsetY);
	}
        return null;
    }

    public void SetUpDragSource(DragSource ds) {
        JPnlLine line = ds.m_codelet;

        // Remove the targeted line.
        line.Remove();

        // if it is an event, remove its representation in JPnlScriptlet's
        // record
        if (line instanceof JPnlLineEvent){
            JPnlLineEvent evt = (JPnlLineEvent)line;
            m_lEvents.remove(evt);
        }

        // remove any PEventNull as a result
        ArrayList<JPnlLineEvent> evts2Remove = new ArrayList<JPnlLineEvent>(0);
        for (JPnlLineEvent lEvt: m_lEvents) {
            if (lEvt instanceof JPnlLineEventNull && lEvt.GetPEvent().IsEmpty()) {
                evts2Remove.add(lEvt);
            }
        }
        for (JPnlLineEvent lEvt: evts2Remove) {
            UnbindEvent(lEvt);
            lEvt.GetPEvent().ClearScript();
            lEvt.GetPEvent().DeRegister();
        }

        // Remove the line list of any empty events
        for (JPnlLineEvent lEvt: m_lEvents) {
            if (lEvt.GetPEvent().IsEmpty())
                lEvt.ClearLineList();
            lEvt.ReinitControls();
        }
        // Update and repaint
        scriptletPane.updateUI();
    }

    public void ReleaseDragSource(JPnlLine source)
    {
    }

    /**
     * Set optimal size for its preferred size
     */
    public void SetOptimalSize()
    {
        // record the max x and y spanned by the PEvents
        int maxx = 0;
        int maxy = 0;
        for (JPnlLineEvent evt: m_lEvents) {
            Point3D p = evt.GetPEvent().GetCoords();
            maxx = Math.max(maxx, p.x + evt.getWidth()+MARGIN_X);
            maxy = Math.max(maxy, p.y + evt.getHeight()+MARGIN_Y);
        }
        setPreferredSize(new Dimension(maxx, maxy));
    }

    /**
     * Binds a new event
     * @param evt The event to be bound
     * @param pt The location where the bound event is to be bound, null if it
     *           is triggered by selecting different objects
     * @return A line control that can be used to remove the event later.
     */
    public JPnlLineEvent BindEvent(PEvent evt, Point3D pt)
    {
        boolean isPtNull = false;

        // Is the point null?  If it is, then we must recover the coordinate from the
	// event.  If the event is null, we return early.
	if(pt == null)
        {
            isPtNull = true;
            pt = evt.GetCoords();
            if(pt == null)
                return null;
        }
	else {
            Point3D origCoords = evt.GetCoords();
            if (origCoords == null) {
                // Update the coordinates, as the coordinates are set:
                evt.SetCoords(pt);
                evt.Register();
            }
            else {
                // Update the x, y coordinates, as the coordinates are set:
                evt.SetCoords(new Point3D(pt.x, pt.y, evt.GetCoords().z));
                evt.Register();
            }
        }
        // Already bound?
        JPnlLineEvent lEvt = null;
        for(JPnlLineEvent cur : m_lEvents)
            if(cur.GetPEvent() == evt)
            {
                lEvt = cur;
                break;
            }

        if(lEvt == null)
        {
            lEvt = evt.GetGui(m_l);
            lEvt.InitControls();
            m_lEvents.add(lEvt);
            scriptletPane.add(lEvt, c);
        }

        int origLayer = -1;
        for (JPnlLineEvent cur: m_lEvents) {
            if (cur.GetPEvent() == evt) {
                if (cur.GetPEvent().GetCoords() != null) {
                    origLayer = cur.GetPEvent().GetCoords().z;  // back up orig layer
                }
                // update the z-position in both Pevent and jpnlLineEvent objects
                if (!isPtNull) {
                    pt.z = m_lEvents.size();
                }
                cur.GetPEvent().SetCoords(pt);
                scriptletPane.setLayer(cur, pt.z);
                break;
            }
        }

        for (JPnlLineEvent cur: m_lEvents) {
            if (cur.GetPEvent() != evt) {
                Point3D p = cur.GetPEvent().GetCoords();
                if (p != null && origLayer != -1 && p.z <= evt.GetCoords().z && p.z >= origLayer) {
                    p.z = p.z - 1;
                    cur.GetPEvent().SetCoords(p);
                    scriptletPane.setLayer(cur, p.z);
                }
            }
        }

        lEvt.setLocation(pt.Get2DPoint());

	return lEvt;
    }

    public void UnbindEvent(JPnlLineEvent evt)
    {
        m_lEvents.remove(evt);
	scriptletPane.remove(evt);
        // adjust the z-orders of the remaining JPnlLineEvents
        // get the layer of evt
        int evtLayer = evt.GetPEvent().GetCoords().z;
        for (JPnlLineEvent remainEvt: this.m_lEvents) {
            if (remainEvt != evt) {
                Point3D p = remainEvt.GetPEvent().GetCoords();
                if (p.z > evtLayer) {
                    p.z -= 1;
                    remainEvt.GetPEvent().SetCoords(p);
                }
            }
        }
	scriptletPane.repaint();
    }

    @Override
    public void UpdateDrag(Point3D pt, JPnlLine line)
    {
        if(pt == null)
        {
            if(m_lastPos != null)
            {
                m_lastPos.ClearHighlight();
                m_lastPos = null;
            }
            return;
	}

        // clear m_lastPos
        m_lastPos = null;

        // clear highlight
        for (JPnlLineEvent evt: m_lEvents) {
            evt.ClearHighlight();
        }

        InsertionPosition bestLastPos = null;
        int bestLastPosEvtZ = -1;
        for(JPnlLineEvent evt : m_lEvents)
        {
            // Completely different situation when the input is of JPnlLineEvent type:
            // We always accept this as a drop type.
            if(line instanceof JPnlLineEvent)
		return;

            InsertionPosition pos = null;
            double minDist = 100000;
            // try all the 4 corners
            // top left
            InsertionPosition pos1 = evt.GetInsertionPosition(
				new Point(
					pt.x - evt.getX(),
					pt.y - evt.getY()
				),
				line.GetStatement()
			);
            if (pos1 != null) {
                minDist = pos1.m_distance;
                pos = pos1;
            }
            // top right
            InsertionPosition pos2 = evt.GetInsertionPosition(
				new Point(
					pt.x + line.getWidth() - evt.getX(),
					pt.y - evt.getY()
				),
				line.GetStatement()
			);
            if (pos2 != null && pos2.m_distance < minDist) {
                minDist = pos2.m_distance;
                pos = pos2;
            }
            if (pos == null) {
                // bottom left
                InsertionPosition pos3 = evt.GetInsertionPosition(
                                    new Point(
                                            pt.x - evt.getX(),
                                            pt.y + line.getHeight() - evt.getY()
                                    ),
                                    line.GetStatement()
                            );
                if (pos3 != null && pos3.m_distance < minDist) {
                    minDist = pos3.m_distance;
                    pos = pos3;
                }
                // bottom right
                InsertionPosition pos4 = evt.GetInsertionPosition(
                                    new Point(
                                            pt.x + line.getWidth() - evt.getX(),
                                            pt.y + line.getHeight() - evt.getY()
                                    ),
                                    line.GetStatement()
                            );
                if (pos4 != null && pos4.m_distance < minDist) {
                    minDist = pos4.m_distance;
                    pos = pos4;
                }
            }
            // if pos is still null, try the last resort - center
            if (pos == null) {
                InsertionPosition pos5 = evt.GetInsertionPosition(
                                    new Point(
                                            pt.x + line.getWidth()/2 - evt.getX(),
                                            pt.y + line.getHeight()/2 - evt.getY()
                                    ),
                                    line.GetStatement()
                            );
                if (pos5 != null && pos5.m_distance < minDist) {
                    minDist = pos5.m_distance;
                    pos = pos5;
                }
            }

            if (pos != null) {
                int z = evt.GetPEvent().GetCoords().z;
                if (bestLastPosEvtZ == -1 || bestLastPosEvtZ < z) {
                    bestLastPos = pos;
                    bestLastPosEvtZ = z;
                }
            }
	}
        if (bestLastPos != null) {
            bestLastPos.Highlight();
            m_lastPos = bestLastPos;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(m_dragSource == null) {
            return;
        }
        JLayeredPane layered = getRootPane().getLayeredPane();
        layered.remove(m_dragSource.m_codelet);
	if(lastDrag != null)
	{
            lastDrag.StopDrag(GetRelativePoint(lastDrag, e), m_dragSource.m_codelet);
            lastDrag = null;
	}
        else {
            // delete everything in the event if it is dragged away from
            // the scriptlet window
            JPnlLine line = m_dragSource.m_codelet;
            if(line instanceof JPnlLineEvent)
            {
                if (contains(e.getPoint())) {
                    JPnlLineEvent lineEvt = (JPnlLineEvent)line;
                    // This corresponds to the case when we click on the arg
                    // input of event codelet
                    Point p1 = e.getPoint();
                    Point p2 = pCSDT.Utility.MakeRelative(p1, lineEvt);
                    lineEvt.GetPEvent().SetCoords(new Point3D(p1.x-p2.x, p1.y-p2.y, lineEvt.GetPEvent().GetCoords().z));
                }
                else {
                    JPnlLineEvent lineEvt = (JPnlLineEvent)line;
                    // remove the (x,y) coordiates associated with the lineEvt
                    // and then all its scripts
                    lineEvt.GetPEvent().SetCoords(null);
                    lineEvt.ClearLineList();
                    lineEvt.GetPEvent().ClearScript();
                    lineEvt.GetPEvent().DeRegister();
                }
            }
        }
	m_dragSource = null;
	layered.repaint();
    }

    @Override
    public void StopDrag(Point3D pt, JPnlLine line)
    {
        if(line instanceof JPnlLineEvent)
	{
            JPnlLineEvent lineEvt = (JPnlLineEvent)line;
            BindEvent(lineEvt.GetPEvent(), pt);
	}
	else if(m_lastPos != null)
	{
            m_lastPos.Insert();
            m_lastPos.ClearHighlight();

            JPnlLine ln = m_lastPos.GetGui();
            assert ln != null;

            JPnlLineEvent evt = ln.GetRoot();
            if(evt != null)
		evt.ReinitControls();
            m_lastPos = null;
            scriptletPane.updateUI();
	}
	else
	{
            // Drag target is empty, create a new null root.
            PEventNull nullRoot = m_boundObj.CreateNullEvent();
            nullRoot.GetListener().AppendChild(line.GetStatement());
            BindEvent(nullRoot, pt);
	}
        // update the optimal size of the scriptlet panel
        SetOptimalSize();
        doLayout();
        updateUI();
    }

    @Override
    public void BindObject(PObject objs)
    {
        m_boundObj = objs;
        // update the optimal size of the scriptlet window
        SetOptimalSize();
        doLayout();
        updateUI();
    }
}
